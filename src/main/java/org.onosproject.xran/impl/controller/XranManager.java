/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.xran.impl.controller;

import com.google.common.collect.Sets;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.IpAddress;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.config.Config;
import org.onosproject.net.config.ConfigFactory;
import org.onosproject.net.config.NetworkConfigEvent;
import org.onosproject.net.config.NetworkConfigListener;
import org.onosproject.net.config.NetworkConfigRegistry;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.net.config.basics.SubjectFactories;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import org.onosproject.xran.XranDeviceAgent;
import org.onosproject.xran.XranDeviceListener;
import org.onosproject.xran.XranHostAgent;
import org.onosproject.xran.XranHostListener;
import org.onosproject.xran.XranPacketProcessor;
import org.onosproject.xran.XranService;
import org.onosproject.xran.XranStore;
import org.onosproject.xran.asn1lib.api.CRNTI;
import org.onosproject.xran.asn1lib.api.ECGI;
import org.onosproject.xran.asn1lib.api.ERABID;
import org.onosproject.xran.asn1lib.api.ERABParams;
import org.onosproject.xran.asn1lib.api.ERABParamsItem;
import org.onosproject.xran.asn1lib.api.Hysteresis;
import org.onosproject.xran.asn1lib.api.MeasID;
import org.onosproject.xran.asn1lib.api.MeasObject;
import org.onosproject.xran.asn1lib.api.PCIARFCN;
import org.onosproject.xran.asn1lib.api.PropScell;
import org.onosproject.xran.asn1lib.api.QOffsetRange;
import org.onosproject.xran.asn1lib.api.RadioRepPerServCell;
import org.onosproject.xran.asn1lib.api.ReportConfig;
import org.onosproject.xran.asn1lib.api.SchedMeasRepPerServCell;
import org.onosproject.xran.asn1lib.api.TimeToTrigger;
import org.onosproject.xran.asn1lib.api.TrafficSplitPercentage;
import org.onosproject.xran.asn1lib.ber.types.BerBoolean;
import org.onosproject.xran.asn1lib.ber.types.BerEnum;
import org.onosproject.xran.asn1lib.ber.types.BerInteger;
import org.onosproject.xran.asn1lib.pdu.BearerAdmissionRequest;
import org.onosproject.xran.asn1lib.pdu.BearerAdmissionResponse;
import org.onosproject.xran.asn1lib.pdu.BearerAdmissionStatus;
import org.onosproject.xran.asn1lib.pdu.BearerReleaseInd;
import org.onosproject.xran.asn1lib.pdu.CellConfigReport;
import org.onosproject.xran.asn1lib.pdu.CellConfigRequest;
import org.onosproject.xran.asn1lib.pdu.HOComplete;
import org.onosproject.xran.asn1lib.pdu.HOFailure;
import org.onosproject.xran.asn1lib.pdu.HORequest;
import org.onosproject.xran.asn1lib.pdu.L2MeasConfig;
import org.onosproject.xran.asn1lib.pdu.PDCPMeasReportPerUe;
import org.onosproject.xran.asn1lib.pdu.RRCMeasConfig;
import org.onosproject.xran.asn1lib.pdu.RRMConfig;
import org.onosproject.xran.asn1lib.pdu.RRMConfigStatus;
import org.onosproject.xran.asn1lib.pdu.RXSigMeasReport;
import org.onosproject.xran.asn1lib.pdu.RadioMeasReportPerCell;
import org.onosproject.xran.asn1lib.pdu.RadioMeasReportPerUE;
import org.onosproject.xran.asn1lib.pdu.ScellAdd;
import org.onosproject.xran.asn1lib.pdu.ScellAddStatus;
import org.onosproject.xran.asn1lib.pdu.ScellDelete;
import org.onosproject.xran.asn1lib.pdu.SchedMeasReportPerCell;
import org.onosproject.xran.asn1lib.pdu.SchedMeasReportPerUE;
import org.onosproject.xran.asn1lib.pdu.TrafficSplitConfig;
import org.onosproject.xran.asn1lib.pdu.UEAdmissionRequest;
import org.onosproject.xran.asn1lib.pdu.UEAdmissionResponse;
import org.onosproject.xran.asn1lib.pdu.UEAdmissionStatus;
import org.onosproject.xran.asn1lib.pdu.UECapabilityEnquiry;
import org.onosproject.xran.asn1lib.pdu.UECapabilityInfo;
import org.onosproject.xran.asn1lib.pdu.UEContextUpdate;
import org.onosproject.xran.asn1lib.pdu.UEReconfigInd;
import org.onosproject.xran.asn1lib.pdu.UEReleaseInd;
import org.onosproject.xran.asn1lib.pdu.XrancPdu;
import org.onosproject.xran.impl.XranConfig;
import org.onosproject.xran.impl.entities.RnibCell;
import org.onosproject.xran.impl.entities.RnibLink;
import org.onosproject.xran.impl.entities.RnibUe;
import org.onosproject.xran.impl.identifiers.ContextUpdateHandler;
import org.onosproject.xran.impl.identifiers.EcgiCrntiPair;
import org.onosproject.xran.impl.identifiers.LinkId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.onosproject.net.DeviceId.deviceId;
import static org.onosproject.xran.impl.controller.XranChannelHandler.getSctpMessage;
import static org.onosproject.xran.impl.entities.RnibCell.decodeDeviceId;
import static org.onosproject.xran.impl.entities.RnibCell.uri;
import static org.onosproject.xran.impl.entities.RnibUe.hostIdtoUEId;

/**
 * Created by dimitris on 7/20/17.
 */
@Component(immediate = true)
@Service
public class XranManager implements XranService {
    protected static final String XRAN_APP_ID = "org.onosproject.xran";
    protected static final Class<XranConfig> CONFIG_CLASS = XranConfig.class;

    protected static final Logger log =
            LoggerFactory.getLogger(XranManager.class);

    /* CONFIG */
    protected final InternalNetworkConfigListener configListener =
            new InternalNetworkConfigListener();

    /* VARIABLES */
    protected final XranServer xranServer = new XranServer();
    protected XranConfig xranConfig;
    protected ApplicationId appId;
    protected int northboundTimeout;

    /* Services */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkConfigRegistry registry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkConfigService configService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected XranStore xranStore;

    protected ConfigFactory<ApplicationId, XranConfig> xranConfigFactory =
            new ConfigFactory<ApplicationId, XranConfig>(
                    SubjectFactories.APP_SUBJECT_FACTORY, CONFIG_CLASS, "xran") {
                @Override
                public XranConfig createConfig() {
                    return new XranConfig();
                }
            };

    /* MAPS */
    protected ConcurrentMap<IpAddress, ECGI> legitCells = new ConcurrentHashMap<>();
    protected ConcurrentMap<ECGI, SynchronousQueue<String>> hoMap = new ConcurrentHashMap<>();
    protected ConcurrentMap<ECGI, SynchronousQueue<String>> rrmCellMap = new ConcurrentHashMap<>();
    protected ConcurrentMap<CRNTI, SynchronousQueue<String>> scellAddMap = new ConcurrentHashMap<>();
    // Map used to keep messages in pairs (HO Complete - CTX Update, Adm Status - CTX Update)
    protected ConcurrentMap<EcgiCrntiPair, ContextUpdateHandler> contextUpdateMap = new ConcurrentHashMap<>();

    /* QUEUE */
    protected BlockingQueue<Long> ueIdQueue = new LinkedBlockingQueue<>();

    /* AGENTS */
    protected InternalXranDeviceAgent deviceAgent = new InternalXranDeviceAgent();
    protected InternalXranHostAgent hostAgent = new InternalXranHostAgent();
    protected InternalXranPacketAgent packetAgent = new InternalXranPacketAgent();

    /* LISTENERS */
    protected Set<XranDeviceListener> xranDeviceListeners = new CopyOnWriteArraySet<>();
    protected Set<XranHostListener> xranHostListeners = new CopyOnWriteArraySet<>();
    protected InternalDeviceListener deviceListener = new InternalDeviceListener();
    protected InternalHostListener hostListener = new InternalHostListener();

    @Activate
    public void activate() {
        appId = coreService.registerApplication(XRAN_APP_ID);

        configService.addListener(configListener);
        registry.registerConfigFactory(xranConfigFactory);
        deviceService.addListener(deviceListener);
        hostService.addListener(hostListener);

        xranStore.setController(this);

        log.info("XRAN XranServer v5 Started");
    }

    @Deactivate
    public void deactivate() {
        deviceService.removeListener(deviceListener);
        hostService.removeListener(hostListener);
        configService.removeListener(configListener);
        registry.unregisterConfigFactory(xranConfigFactory);

        cleanup();

        log.info("XRAN XranServer v5 Stopped");
    }

    /**
     * Cleanup when application is deactivated.
     */
    private void cleanup() {
        xranStore.getUeNodes().forEach(ue -> xranHostListeners.forEach(l -> l.hostRemoved(ue.getHostId())));

        xranStore.getCellNodes().forEach(cell -> xranDeviceListeners
                .forEach(l -> l.deviceRemoved(deviceId(uri(cell.getEcgi())))));

        xranServer.stop();

        legitCells.clear();
        hoMap.clear();
        rrmCellMap.clear();
        scellAddMap.clear();
        contextUpdateMap.clear();
        ueIdQueue.clear();
        xranDeviceListeners.clear();
        xranHostListeners.clear();
    }

    @Override
    public Optional<SynchronousQueue<String>> sendHoRequest(RnibLink linkT, RnibLink linkS) {
        ECGI ecgiT = linkT.getLinkId().getEcgi(),
                ecgiS = linkS.getLinkId().getEcgi();

        Optional<ChannelHandlerContext> ctxT = xranStore.getCtx(ecgiT),
                ctxS = xranStore.getCtx(ecgiS);

        return xranStore.getCrnti(linkT.getLinkId().getUeId()).map(crnti -> {
            SynchronousQueue<String> queue = new SynchronousQueue<>();

            XrancPdu xrancPdu = HORequest.constructPacket(crnti, ecgiS, ecgiT);

            // temporary map that has ECGI source of a handoff to a queue waiting for REST response.
            hoMap.put(ecgiS, queue);

            ctxT.ifPresent(ctx -> ctx.writeAndFlush(getSctpMessage(xrancPdu)));
            ctxS.ifPresent(ctx -> ctx.writeAndFlush(getSctpMessage(xrancPdu)));

            // FIXME: only works for one HO at a time.
            try {
                ueIdQueue.put(linkT.getLinkId().getUeId());
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getFullStackTrace(e));
            }

            return Optional.of(queue);
        }).orElse(Optional.empty());
    }

    @Override
    public void addListener(XranDeviceListener listener) {
        xranDeviceListeners.add(listener);
    }

    @Override
    public void addListener(XranHostListener listener) {
        xranHostListeners.add(listener);
    }

    @Override
    public void removeListener(XranDeviceListener listener) {
        xranDeviceListeners.remove(listener);
    }

    @Override
    public void removeListener(XranHostListener listener) {
        xranHostListeners.remove(listener);
    }

    @Override
    public int getNorthboundTimeout() {
        return northboundTimeout;
    }

    @Override
    public Optional<SynchronousQueue<String>> sendModifiedRrm(RRMConfig rrmConfig) {
        ECGI ecgi = rrmConfig.getEcgi();
        Optional<ChannelHandlerContext> optionalCtx = xranStore.getCtx(ecgi);

        // if ctx exists then create the queue and send the message
        return optionalCtx.flatMap(ctx -> {
            XrancPdu pdu;
            pdu = RRMConfig.constructPacket(rrmConfig);
            ctx.writeAndFlush(getSctpMessage(pdu));
            SynchronousQueue<String> queue = new SynchronousQueue<>();
            rrmCellMap.put(ecgi, queue);
            return Optional.of(queue);
        });
    }

    @Override
    public Optional<SynchronousQueue<String>> sendScellAdd(RnibLink link) {
        RnibCell secondaryCell = link.getLinkId().getCell();
        // find primary cell
        return xranStore.getPrimaryCell(link.getLinkId().getUe()).flatMap(primaryCell -> {
                    ECGI primaryEcgi = primaryCell.getEcgi();
                    // get ctx for the primary cell
                    return xranStore.getCtx(primaryEcgi).flatMap(ctx ->
                            // check if configuration exists
                            secondaryCell.getOptConf().flatMap(cellReport -> {
                                        PCIARFCN pciarfcn = new PCIARFCN();
                                        pciarfcn.setPci(cellReport.getPci());
                                        pciarfcn.setEarfcnDl(cellReport.getEarfcnDl());

                                        PropScell propScell = new PropScell();
                                        propScell.setPciArfcn(pciarfcn);

                                        // search crnti of specific UE
                                        return xranStore.getCrnti(link.getLinkId().getUeId()).flatMap(crnti -> {
                                            SynchronousQueue<String> queue;
                                            XrancPdu pdu = ScellAdd
                                                    .constructPacket(primaryEcgi, crnti, propScell);

                                            ctx.writeAndFlush(getSctpMessage(pdu));
                                            queue = new SynchronousQueue<>();
                                            scellAddMap.put(crnti, queue);

                                            return Optional.of(queue);
                                        });
                                    }
                            )
                    );
                }
        );
    }

    @Override
    public boolean sendScellDelete(RnibLink link) {
        RnibCell secondaryCell = link.getLinkId().getCell();
        // find primary cell
        return xranStore.getPrimaryCell(link.getLinkId().getUe()).map(primaryCell -> {
            ECGI primaryEcgi = primaryCell.getEcgi();
            // get ctx for the primary cell
            return xranStore.getCtx(primaryEcgi).map(ctx ->
                    // check if config exists
                    secondaryCell.getOptConf().map(cellReport -> {
                        PCIARFCN pciarfcn = new PCIARFCN();
                        pciarfcn.setPci(cellReport.getPci());
                        pciarfcn.setEarfcnDl(cellReport.getEarfcnDl());

                        // check if crnti for UE exists
                        return xranStore.getCrnti(link.getLinkId().getUeId()).map(crnti -> {
                            XrancPdu pdu = ScellDelete.constructPacket(primaryEcgi, crnti, pciarfcn);
                            ctx.writeAndFlush(getSctpMessage(pdu));
                            link.setType(RnibLink.Type.NON_SERVING);
                            return true;
                        }).orElse(false);
                    }).orElse(false)
            ).orElse(false);
        }).orElse(false);
    }

    /**
     * Timer to delete UE after being IDLE.
     *
     * @param ue UE entity
     */
    private void restartTimer(RnibUe ue) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ue.setExecutor(executor);
        executor.schedule(
                () -> {
                    if (ue.getState().equals(RnibUe.State.IDLE)) {
                        hostAgent.removeConnectedHost(ue);
                        log.info("UE is removed after {} ms of IDLE", xranConfig.getIdleUeRemoval());
                    } else {
                        log.info("UE not removed cause its ACTIVE");
                    }
                },
                xranConfig.getIdleUeRemoval(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Timer to delete LINK after not receiving measurements.
     *
     * @param link LINK entity
     */
    private void restartTimer(RnibLink link) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        link.setExecutor(executor);
        executor.schedule(
                () -> {
                    LinkId linkId = link.getLinkId();
                    xranStore.removeLink(linkId);
                    log.info("Link is removed after not receiving Meas Reports for {} ms",
                            xranConfig.getNoMeasLinkRemoval());
                },
                xranConfig.getNoMeasLinkRemoval(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Request measurement configuration field of specified UE.
     *
     * @param primary primary CELL
     * @param ue      UE entity
     */
    // TODO
    private void populateMeasConfig(RnibCell primary, RnibUe ue) {
        RRCMeasConfig.MeasObjects measObjects = new RRCMeasConfig.MeasObjects();
        RRCMeasConfig.MeasIds measIds = new RRCMeasConfig.MeasIds();
        // get ctx for cell
        xranStore.getCtx(primary.getEcgi()).ifPresent(ctx -> {
            // iterate through all cells
            final int[] index = {0};
            xranStore.getCellNodes().forEach(cell ->
                    // set pciarfcn if config exists
                    cell.getOptConf().ifPresent(cellReport -> {
                                // PCIARFCN
                                PCIARFCN pciarfcn = new PCIARFCN();
                                pciarfcn.setPci(cellReport.getPci());
                                pciarfcn.setEarfcnDl(cellReport.getEarfcnDl());

                                // MEAS OBJECT
                                MeasObject measObject = new MeasObject();
                                MeasObject.MeasCells measCells = new MeasObject.MeasCells();
                                measObject.setMeasCells(measCells);
                                measObject.setDlFreq(cellReport.getEarfcnDl());
                                measCells.setPci(cellReport.getPci());
                                measCells.setCellIndividualOffset(new QOffsetRange(0));
                                measObjects.getMeasObject().add(measObject);

                                // MEAS ID
                                MeasID measID = new MeasID();
                                MeasID.Action action = new MeasID.Action();
                                action.setHototarget(new BerBoolean(false));
                                measID.setAction(action);
                                measID.setReportconfigId(new BerInteger(0));
                                measID.setMeasobjectId(new BerInteger(index[0]++));
                                measIds.getMeasID().add(measID);
                            }
                    )
            );
            // REPORT CONFIG

            RRCMeasConfig.ReportConfigs reportConfigs = new RRCMeasConfig.ReportConfigs();
            ReportConfig reportConfig = reportConfigs.getReportConfig().get(0);

            reportConfig.setReportQuantity(new BerEnum(0));
            reportConfig.setTriggerQuantity(new BerEnum(0));

            ReportConfig.ReportParams reportParams = new ReportConfig.ReportParams();
            reportParams.setHysteresis(new Hysteresis(0));
            reportParams.setParams(new ReportConfig.ReportParams.Params());
            reportParams.setTimetotrigger(new TimeToTrigger(0));

            reportConfig.setReportParams(reportParams);

            // construct a rx sig meas conf packet
            XrancPdu xrancPdu = RRCMeasConfig.constructPacket(
                    primary.getEcgi(),
                    ue.getCrnti(),
                    measObjects,
                    reportConfigs,
                    measIds,
                    xranConfig.getRxSignalInterval()
            );
            ue.setMeasConfig(xrancPdu.getBody().getRRCMeasConfig());
            ctx.writeAndFlush(getSctpMessage(xrancPdu));
        });
    }

    public void panic(XrancPdu recvPdu) {
        throw new IllegalArgumentException("Received illegal packet: " + recvPdu.toString());
    }

    /**
     * Internal device listener.
     */
    class InternalDeviceListener implements DeviceListener {

        @Override
        public void event(DeviceEvent event) {
            log.info("Device Event {}", event);
            switch (event.type()) {
                case DEVICE_ADDED: {
                    try {
                        ECGI ecgi = decodeDeviceId(event.subject().id());
                        // move this to a routine service
                        xranStore.getCell(ecgi).ifPresent(cell -> {
                            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                            executor.scheduleAtFixedRate(
                                    () -> {
                                        // populate config if it does not exist
                                        if (!cell.getOptConf().isPresent()) {
                                            // if channel context is present then send the config request
                                            xranStore.getCtx(ecgi).ifPresent(ctx -> {
                                                XrancPdu xrancPdu = CellConfigRequest.constructPacket(ecgi);
                                                ctx.writeAndFlush(getSctpMessage(xrancPdu));
                                            });
                                        } else {
                                            // iterate through all UEs
                                            xranStore.getUeNodes().forEach(ue -> xranStore.getPrimaryCell(ue)
                                                    .ifPresent(primaryCell -> populateMeasConfig(primaryCell, ue)));

                                            // send l2 meas interval
                                            xranStore.getCtx(ecgi).ifPresent(ctx -> {
                                                XrancPdu xrancPdu = L2MeasConfig
                                                        .constructPacket(ecgi, xranConfig.getL2MeasInterval());
                                                cell.getMeasConfig().setL2MeasConfig(xrancPdu.getBody()
                                                        .getL2MeasConfig());
                                                SctpMessage sctpMessage = getSctpMessage(xrancPdu);
                                                ctx.writeAndFlush(sctpMessage);

                                                executor.shutdown();
                                            });
                                        }
                                    },
                                    0,
                                    xranConfig.getConfigRequestInterval(),
                                    TimeUnit.SECONDS
                            );
                        });
                    } catch (IOException e) {
                        log.error(ExceptionUtils.getFullStackTrace(e));
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    /**
     * Internal host listener.
     */
    class InternalHostListener implements HostListener {

        @Override
        public void event(HostEvent event) {
            log.info("Host Event {}", event);
            switch (event.type()) {
                case HOST_ADDED:
                case HOST_MOVED: {
                    xranStore.getUe(hostIdtoUEId(event.subject().id())).ifPresent(ue -> xranStore.getPrimaryCell(ue)
                            .ifPresent(cell -> {
                                ue.setMeasConfig(null);

                                // move this to a routine service
                                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                                executor.scheduleAtFixedRate(
                                        () -> {
                                            if (cell.getVersion() >= 3) {
                                                if (!Optional.ofNullable(ue.getCapability()).isPresent()) {
                                                    xranStore.getCtx(cell.getEcgi()).ifPresent(ctx -> {
                                                        XrancPdu xrancPdu = UECapabilityEnquiry.constructPacket(
                                                                cell.getEcgi(),
                                                                ue.getCrnti());
                                                        ctx.writeAndFlush(getSctpMessage(xrancPdu));
                                                    });
                                                } else {
                                                    executor.shutdown();
                                                }
                                            } else {
                                                executor.shutdown();
                                            }

                                        },
                                        0,
                                        xranConfig.getConfigRequestInterval(),
                                        TimeUnit.MILLISECONDS
                                );
                                if (ue.getMeasConfig() == null) {
                                    populateMeasConfig(cell, ue);
                                }
                            }));
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    /**
     * Internal xran device agent.
     */
    public class InternalXranDeviceAgent implements XranDeviceAgent {

        private final Logger log = LoggerFactory.getLogger(InternalXranDeviceAgent.class);

        @Override
        public boolean addConnectedCell(String host, ChannelHandlerContext ctx) {
            log.info("addConnectedCell: {}", host);
            // check configuration if the cell is inside the accepted list
            return Optional.ofNullable(legitCells.get(IpAddress.valueOf(host))).map(ecgi -> {
                log.info("Device exists in configuration; registering...");
                // check if cell is not already registered
                if (!xranStore.getCell(ecgi).isPresent()) {
                    RnibCell storeCell = new RnibCell();
                    storeCell.setEcgi(ecgi);
                    xranStore.storeCtx(storeCell, ctx);
                    xranDeviceListeners.forEach(l -> l.deviceAdded(storeCell));
                    return true;
                }
                return false;
            }).orElseGet(() -> {
                        log.error("Device is not a legit source; ignoring...");
                        ctx.close();
                        return false;
                    }
            );
        }

        @Override
        public boolean removeConnectedCell(String host) {
            log.info("removeConnectedCell: {}", host);
            ECGI ecgi = legitCells.get(IpAddress.valueOf(host));

            xranStore.getLinks(ecgi).forEach(rnibLink -> {
                rnibLink.getLinkId().getUe().setState(RnibUe.State.IDLE);
                restartTimer(rnibLink.getLinkId().getUe());
                xranStore.removeLink(rnibLink.getLinkId());
            });

            if (xranStore.removeCell(ecgi)) {
                xranDeviceListeners.forEach(l -> l.deviceRemoved(deviceId(uri(ecgi))));
                return true;
            }
            return false;
        }
    }

    /**
     * Internal xran host agent.
     */
    public class InternalXranHostAgent implements XranHostAgent {

        @Override
        public boolean addConnectedHost(RnibUe ue, RnibCell cell, ChannelHandlerContext ctx) {
            log.info("addConnectedHost: {}", ue);
            if (ue.getId() != null && xranStore.getUe(ue.getId()).isPresent()) {
                xranStore.putPrimaryLink(cell, ue);

                Set<ECGI> ecgiSet = Sets.newConcurrentHashSet();

                xranStore.getLinks(ue.getId())
                        .stream()
                        .filter(l -> l.getType().equals(RnibLink.Type.SERVING_PRIMARY))
                        .findFirst()
                        .ifPresent(l -> ecgiSet.add(l.getLinkId().getEcgi()));

                xranHostListeners.forEach(l -> l.hostAdded(ue, ecgiSet));
                return true;
            } else {
                xranStore.storeUe(cell, ue);
                xranStore.putPrimaryLink(cell, ue);

                Set<ECGI> ecgiSet = Sets.newConcurrentHashSet();
                ecgiSet.add(cell.getEcgi());
                xranHostListeners.forEach(l -> l.hostAdded(ue, ecgiSet));
                return true;
            }

        }

        @Override
        public boolean removeConnectedHost(RnibUe ue) {
            log.info("removeConnectedHost: {}", ue);
            xranStore.getLinks(ue.getId()).forEach(rnibLink -> xranStore.removeLink(rnibLink.getLinkId()));
            if (xranStore.removeUe(ue.getId())) {
                xranHostListeners.forEach(l -> l.hostRemoved(ue.getHostId()));
                return true;
            }
            return false;
        }
    }

    public class InternalXranPacketAgent implements XranPacketProcessor {
        @Override
        public void handlePacket(XrancPdu recvPdu, ChannelHandlerContext ctx)
                throws IOException, InterruptedException {
            int apiID = recvPdu.getHdr().getApiId().intValue();
            log.debug("Received message: {}", recvPdu);
            switch (apiID) {
                // Cell Config Report
                case 1: {
                    CellConfigReport report = recvPdu.getBody().getCellConfigReport();
                    handleCellconfigreport(report, recvPdu.getHdr().getVer().toString());
                    break;
                }
                // UE Admission Request
                case 2: {
                    UEAdmissionRequest ueAdmissionRequest = recvPdu.getBody().getUEAdmissionRequest();
                    handleUeadmissionRequest(ueAdmissionRequest, ctx);
                    break;
                }
                // UE Admission Status
                case 4: {
                    UEAdmissionStatus ueAdmissionStatus = recvPdu.getBody().getUEAdmissionStatus();
                    handleAdmissionStatus(ueAdmissionStatus, ctx);
                    break;
                }
                // UE Context Update
                case 5: {
                    UEContextUpdate ueContextUpdate = recvPdu.getBody().getUEContextUpdate();
                    handleUeContextUpdate(ueContextUpdate, ctx);
                    break;
                }
                // UE Reconfig Ind
                case 6: {
                    UEReconfigInd ueReconfigInd = recvPdu.getBody().getUEReconfigInd();
                    handleUeReconfigInd(ueReconfigInd);
                    break;
                }
                // UE Release Ind
                case 7: {
                    // If xRANc wants to deactivate UE, we pass UEReleaseInd from xRANc to eNB.
                    UEReleaseInd ueReleaseInd = recvPdu.getBody().getUEReleaseInd();
                    handleUeReleaseInd(ueReleaseInd);
                    break;
                }
                // Bearer Admission Request
                case 8: {
                    BearerAdmissionRequest bearerAdmissionRequest = recvPdu.getBody().getBearerAdmissionRequest();
                    handleBearerAdmissionRequest(bearerAdmissionRequest, ctx);
                    break;
                }
                // Bearer Admission Status
                case 10: {
                    BearerAdmissionStatus bearerAdmissionStatus = recvPdu.getBody().getBearerAdmissionStatus();
                    // TODO: implement
                    break;
                }
                // Bearer Release Ind
                case 11: {
                    BearerReleaseInd bearerReleaseInd = recvPdu.getBody().getBearerReleaseInd();
                    handleBearerReleaseInd(bearerReleaseInd);
                    break;
                }
                // HO Failure
                case 13: {
                    HOFailure hoFailure = recvPdu.getBody().getHOFailure();
                    handleHoFailure(hoFailure);
                    break;
                }
                // HO Complete
                case 14: {
                    HOComplete hoComplete = recvPdu.getBody().getHOComplete();
                    handleHoComplete(hoComplete, ctx);
                    break;
                }
                // RX Sig Meas Report
                case 15: {
                    RXSigMeasReport rxSigMeasReport = recvPdu.getBody().getRXSigMeasReport();
                    handleRxSigMeasReport(rxSigMeasReport);
                    break;
                }
                // Radio Meas Report per UE
                case 17: {
                    RadioMeasReportPerUE radioMeasReportPerUE = recvPdu.getBody().getRadioMeasReportPerUE();
                    handleRadioMeasReportPerUe(radioMeasReportPerUE);
                    break;
                }
                // Radio Meas Report per Cell
                case 18: {
                    RadioMeasReportPerCell radioMeasReportPerCell = recvPdu.getBody().getRadioMeasReportPerCell();
                    handleRadioMeasReportPerCell(radioMeasReportPerCell);
                    break;
                }
                // Sched Meas Report per UE
                case 19: {
                    SchedMeasReportPerUE schedMeasReportPerUE = recvPdu.getBody().getSchedMeasReportPerUE();
                    handleSchedMeasReportPerUe(schedMeasReportPerUE);
                    break;
                }
                // Sched Meas Report per Cell
                case 20: {
                    SchedMeasReportPerCell schedMeasReportPerCell = recvPdu.getBody().getSchedMeasReportPerCell();
                    handleSchedMeasReportPerCell(schedMeasReportPerCell);
                    break;
                }
                // PDCP Meas Report per UE
                case 21: {
                    PDCPMeasReportPerUe pdcpMeasReportPerUe = recvPdu.getBody().getPDCPMeasReportPerUe();
                    handlePdcpMeasReportPerUe(pdcpMeasReportPerUe);
                    break;
                }
                // UE Capability Enquiry
                case 22: {
                    UECapabilityEnquiry ueCapabilityEnquiry = recvPdu.getBody().getUECapabilityEnquiry();
                    handleUecapabilityenquiry(ueCapabilityEnquiry, ctx);
                    break;
                }
                // UE Capability Info
                case 23: {
                    UECapabilityInfo capabilityInfo = recvPdu.getBody().getUECapabilityInfo();
                    handleCapabilityInfo(capabilityInfo);
                    break;
                }
                // Scell Add Status
                case 25: {
                    ScellAddStatus scellAddStatus = recvPdu.getBody().getScellAddStatus();
                    handleScellAddStatus(scellAddStatus);
                    break;
                }
                // RRM Config Status
                case 28: {
                    // Decode RRMConfig Status
                    RRMConfigStatus rrmConfigStatus = recvPdu.getBody().getRRMConfigStatus();
                    handleRrmConfigStatus(rrmConfigStatus);
                    break;
                }
                // SeNB Add
                case 29: {
                    // TODO: implement
                    break;
                }
                // SeNB Add Status
                case 30: {
                    // TODO: implement
                    break;
                }
                // SeNB Delete
                case 31: {
                    // TODO: implement
                    break;
                }
                // Traffic Split Config
                case 32: {
                    TrafficSplitConfig trafficSplitConfig = recvPdu.getBody().getTrafficSplitConfig();
                    handleTrafficSplitConfig(trafficSplitConfig);
                    break;
                }
                // HO Cause
                case 33: {
                    // TODO: implement
                    break;
                }
                case 34: {
                    // TODO: implement
                    break;
                }
                // Cell Config Request
                case 0:
                    // UE Admission Response
                case 3:
                    // Bearer Admission Response
                case 9:
                    // HO Request
                case 12:
                    // L2 Meas Config
                case 16:
                    // Scell Add
                case 24:
                    // Scell Delete
                case 26:
                    // RRM Config
                case 27:
                default: {
                    panic(recvPdu);
                }
            }

        }

        /**
         * Handle Cellconfigreport.
         *
         * @param report  CellConfigReport
         * @param version String version ID
         */
        private void handleCellconfigreport(CellConfigReport report, String version) {
            ECGI ecgi = report.getEcgi();

            xranStore.getCell(ecgi).ifPresent(cell -> {
                cell.setVersion(version);
                cell.setConf(report);
                xranStore.storePciArfcn(cell);
            });
        }

        /**
         * Handle Ueadmissionrequest.
         *
         * @param ueAdmissionRequest UEAdmissionRequest
         * @param ctx                ChannelHandlerContext
         * @throws IOException IO Exception
         */
        private void handleUeadmissionRequest(UEAdmissionRequest ueAdmissionRequest, ChannelHandlerContext ctx)
                throws IOException {
            ECGI ecgi = ueAdmissionRequest.getEcgi();

            xranStore.getCell(ecgi).map(c -> {
                CRNTI crnti = ueAdmissionRequest.getCrnti();
                XrancPdu sendPdu = UEAdmissionResponse.constructPacket(ecgi, crnti, xranConfig.admissionFlag());
                ctx.writeAndFlush(getSctpMessage(sendPdu));
                return 1;
            }).orElseGet(() -> {
                log.warn("Could not find ECGI in registered cells: {}", ecgi);
                return 0;
            });
        }

        /**
         * Handle UEAdmissionStatus.
         *
         * @param ueAdmissionStatus UEAdmissionStatus
         * @param ctx               ChannelHandlerContext
         */
        private void handleAdmissionStatus(UEAdmissionStatus ueAdmissionStatus, ChannelHandlerContext ctx) {
            xranStore.getUe(ueAdmissionStatus.getEcgi(), ueAdmissionStatus.getCrnti()).ifPresent(ue -> {
                if (ueAdmissionStatus.getAdmEstStatus().value.intValue() == 0) {
                    ue.setState(RnibUe.State.ACTIVE);
                } else {
                    ue.setState(RnibUe.State.IDLE);
                }
            });

            if (ueAdmissionStatus.getAdmEstStatus().value.intValue() == 0) {
                EcgiCrntiPair ecgiCrntiPair = EcgiCrntiPair
                        .valueOf(ueAdmissionStatus.getEcgi(), ueAdmissionStatus.getCrnti());
                contextUpdateMap.compute(ecgiCrntiPair, (k, v) -> {
                    if (v == null) {
                        v = new ContextUpdateHandler();
                    }
                    if (v.setAdmissionStatus(ueAdmissionStatus)) {
                        handlePairedPackets(v.getContextUpdate(), ctx, false);
                        v.reset();
                    }
                    return v;
                });
            }
        }

        /**
         * Handle UEContextUpdate.
         *
         * @param ueContextUpdate UEContextUpdate
         * @param ctx             ChannelHandlerContext
         */
        private void handleUeContextUpdate(UEContextUpdate ueContextUpdate, ChannelHandlerContext ctx) {
            EcgiCrntiPair ecgiCrntiPair = EcgiCrntiPair
                    .valueOf(ueContextUpdate.getEcgi(), ueContextUpdate.getCrnti());

            contextUpdateMap.compute(ecgiCrntiPair, (k, v) -> {
                if (v == null) {
                    v = new ContextUpdateHandler();
                }
                if (v.setContextUpdate(ueContextUpdate)) {
                    HOComplete hoComplete = v.getHoComplete();
                    handlePairedPackets(ueContextUpdate, ctx, hoComplete != null);
                    if (hoComplete != null) {
                        try {
                            hoMap.get(hoComplete.getEcgiS()).put("Hand Over Completed");
                        } catch (InterruptedException e) {
                            log.error(ExceptionUtils.getFullStackTrace(e));
                        } finally {
                            hoMap.remove(hoComplete.getEcgiS());
                        }
                    }
                    v.reset();
                }
                return v;
            });
        }

        /**
         * Handle UEReconfigInd.
         *
         * @param ueReconfigInd UEReconfigInd
         */
        private void handleUeReconfigInd(UEReconfigInd ueReconfigInd) {
            Optional<RnibUe> ue = xranStore.getUe(ueReconfigInd.getEcgi(), ueReconfigInd.getCrntiOld());
            Optional<RnibCell> cell = xranStore.getCell(ueReconfigInd.getEcgi());

            if (ue.isPresent() && cell.isPresent()) {
                ue.get().setCrnti(ueReconfigInd.getCrntiNew());
                xranStore.storeCrnti(cell.get(), ue.get());
            } else {
                log.warn("Could not find UE with this CRNTI: {}", ueReconfigInd.getCrntiOld());
            }
        }

        /**
         * Handle UEReleaseInd.
         *
         * @param ueReleaseInd UEReleaseInd
         */
        private void handleUeReleaseInd(UEReleaseInd ueReleaseInd) {
            ECGI ecgi = ueReleaseInd.getEcgi();
            CRNTI crnti = ueReleaseInd.getCrnti();

            // Check if there is an ongoing handoff and only remove if ue is not part of the handoff.
            Long peek = ueIdQueue.peek();
            if (peek != null) {
                EcgiCrntiPair ecgiCrntiPair = xranStore.getCrnti().inverse().get(peek);
                if (ecgiCrntiPair != null && ecgiCrntiPair.equals(EcgiCrntiPair.valueOf(ecgi, crnti))) {
                    return;
                }
            }

            xranStore.getUe(ecgi, crnti).ifPresent(ue -> {
                ue.setState(RnibUe.State.IDLE);
                restartTimer(ue);
            });
        }

        /**
         * Handle BearerAdmissionRequest.
         *
         * @param bearerAdmissionRequest BearerAdmissionRequest
         * @param ctx                    ChannelHandlerContext
         */
        private void handleBearerAdmissionRequest(BearerAdmissionRequest bearerAdmissionRequest,
                                                  ChannelHandlerContext ctx) {
            ECGI ecgi = bearerAdmissionRequest.getEcgi();
            CRNTI crnti = bearerAdmissionRequest.getCrnti();
            ERABParams erabParams = bearerAdmissionRequest.getErabParams();
            xranStore.getLink(ecgi, crnti).ifPresent(link -> link.setBearerParameters(erabParams));

            BerInteger numErabs = bearerAdmissionRequest.getNumErabs();
            // Encode and send Bearer Admission Response
            XrancPdu sendPdu = BearerAdmissionResponse
                    .constructPacket(ecgi, crnti, erabParams, numErabs, xranConfig.bearerFlag());
            ctx.writeAndFlush(getSctpMessage(sendPdu));
        }

        /**
         * Handle BearerReleaseInd.
         *
         * @param bearerReleaseInd bearer release ind
         */
        private void handleBearerReleaseInd(BearerReleaseInd bearerReleaseInd) {
            ECGI ecgi = bearerReleaseInd.getEcgi();
            CRNTI crnti = bearerReleaseInd.getCrnti();

            xranStore.getLink(ecgi, crnti).ifPresent(link -> {
                List<ERABID> erabidsRelease = bearerReleaseInd.getErabIds().getERABID();
                List<ERABParamsItem> erabParamsItem = link.getBearerParameters().getERABParamsItem();

                List<ERABParamsItem> unreleased = erabParamsItem
                        .stream()
                        .filter(item -> {
                            Optional<ERABID> any = erabidsRelease.stream()
                                    .filter(id -> id.equals(item.getId())).findAny();
                            return !any.isPresent();
                        }).collect(Collectors.toList());
                link.getBearerParameters().getERABParamsItem().clear();
                link.getBearerParameters().getERABParamsItem().addAll(new ArrayList<>(unreleased));
            });
        }

        /**
         * Handle HOFailure.
         *
         * @param hoFailure HOFailure
         * @throws InterruptedException ueIdQueue interruption
         */
        private void handleHoFailure(HOFailure hoFailure) throws InterruptedException {
            try {
                hoMap.get(hoFailure.getEcgi())
                        .put("Hand Over Failed with cause: " + hoFailure.getCause());
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getFullStackTrace(e));
            } finally {
                hoMap.remove(hoFailure.getEcgi());
                ueIdQueue.take();
            }
        }

        /**
         * Handle HOComplete.
         *
         * @param hoComplete HOComplete
         * @param ctx        ChannelHandlerContext
         */
        private void handleHoComplete(HOComplete hoComplete, ChannelHandlerContext ctx) {
            EcgiCrntiPair ecgiCrntiPair = EcgiCrntiPair.valueOf(hoComplete.getEcgiT(),
                    hoComplete.getCrntiNew());
            contextUpdateMap.compute(ecgiCrntiPair, (k, v) -> {
                if (v == null) {
                    v = new ContextUpdateHandler();
                }
                if (v.setHoComplete(hoComplete)) {
                    handlePairedPackets(v.getContextUpdate(), ctx, true);

                    try {
                        hoMap.get(hoComplete.getEcgiS()).put("Hand Over Completed");
                    } catch (InterruptedException e) {
                        log.error(ExceptionUtils.getFullStackTrace(e));
                    } finally {
                        hoMap.remove(hoComplete.getEcgiS());
                    }
                    v.reset();
                }
                return v;
            });
        }

        /**
         * Handle RXSigMeasReport.
         *
         * @param rxSigMeasReport RXSigMeasReport
         */
        private void handleRxSigMeasReport(RXSigMeasReport rxSigMeasReport) {
            rxSigMeasReport.getCellMeasReports().getSEQUENCEOF().forEach(
                    cellMeasReport -> cellMeasReport.getRXSigReport().forEach(
                            rxSigReport -> {
                                rxSigMeasReport.getCrnti().getCRNTI().forEach(
                                        crnti -> xranStore.getUe(rxSigMeasReport.getEcgi(), crnti).ifPresent(ue -> {
                                            Long ueId = ue.getId();
                                            xranStore.getCell(rxSigReport.getPciArfcn()).ifPresent(cell -> {
                                                ECGI ecgi = cell.getEcgi();

                                                Optional<RnibLink> link = xranStore.getLink(ecgi, ueId);
                                                if (!link.isPresent()) {
                                                    log.warn("Could not find link between: {}-{} " +
                                                                    "| Creating non-serving link..",
                                                            ecgi, ueId);
                                                    link = xranStore.putNonServingLink(cell, ueId);
                                                }

                                                if (link.isPresent()) {
                                                    if (link.get().getType().equals(RnibLink.Type.NON_SERVING)) {
                                                        restartTimer(link.get());
                                                    }

//                                                    link.get().getMeasurements().setRxSigReport(
//                                                        new RnibLink.Measurements.RXSigReport(
//                                                                rxSigReport.getRsrq(),
//                                                                rxSigReport.getRsrp(),
//                                                                rxSigReport.get...
//                                                        )
//                                                    );
                                                }
                                            });
                                        })
                                );
                            }
                    )
            );
        }

        /**
         * Handle RadioMeasReportPerUE.
         *
         * @param radioMeasReportPerUE RadioMeasReportPerUE
         */
        private void handleRadioMeasReportPerUe(RadioMeasReportPerUE radioMeasReportPerUE) {
            xranStore.getUe(radioMeasReportPerUE.getEcgi(), radioMeasReportPerUE.getCrnti()).ifPresent(ue -> {
                Long ueId = ue.getId();
                List<RadioRepPerServCell> servCells = radioMeasReportPerUE.getRadioReportServCells()
                        .getRadioRepPerServCell();

                servCells.forEach(servCell -> xranStore.getCell(servCell.getEcgi())
                        .ifPresent(cell -> xranStore.getLink(cell.getEcgi(), ueId)
                                .ifPresent(link -> {
                                            RadioRepPerServCell.CqiHist cqiHist = servCell.getCqiHist();
                                            final double[] values = {0, 0, 0};
                                            final int[] i = {1};
                                            cqiHist.getBerInteger().forEach(value -> {
                                                        values[0] = Math.max(values[0], value.intValue());
                                                        values[1] += i[0] * value.intValue();
                                                        values[2] += value.intValue();
                                                        i[0]++;
                                                    }
                                            );

                                            link.getMeasurements().setRadioReport(
                                                    new RnibLink.Measurements.RadioReport(
                                                            new RnibLink.Measurements.RadioReport.Cqi(
                                                                    cqiHist,
                                                                    values[0],
                                                                    values[1] / values[0]
                                                            ),
                                                            servCell.getRiHist(),
                                                            servCell.getPucchSinrHist(),
                                                            servCell.getPuschSinrHist()

                                                    )
                                            );
                                        }
                                )
                        )
                );
            });
        }

        /**
         * Handle RadioMeasReportPerCell.
         *
         * @param radioMeasReportPerCell RadioMeasReportPerCell
         */
        private void handleRadioMeasReportPerCell(RadioMeasReportPerCell radioMeasReportPerCell) {
            xranStore.getCell(radioMeasReportPerCell.getEcgi()).ifPresent(
                    cell -> cell.getMeasurements().setUlInterferenceMeasurement(
                            new RnibCell.Measurements.ULInterferenceMeasurement(
                                    radioMeasReportPerCell.getPuschIntfPowerHist(),
                                    radioMeasReportPerCell.getPucchIntfPowerHist()
                            )
                    )
            );
        }

        /**
         * Handle SchedMeasReportPerUE.
         *
         * @param schedMeasReportPerUE SchedMeasReportPerUE
         */
        private void handleSchedMeasReportPerUe(SchedMeasReportPerUE schedMeasReportPerUE) {
            xranStore.getUe(schedMeasReportPerUE.getEcgi(), schedMeasReportPerUE.getCrnti()).ifPresent(ue -> {
                Long ueId = ue.getId();

                List<SchedMeasRepPerServCell> servCells = schedMeasReportPerUE.getSchedReportServCells()
                        .getSchedMeasRepPerServCell();

                servCells.forEach(servCell -> xranStore.getCell(servCell.getEcgi())
                        .ifPresent(cell -> xranStore.getLink(cell.getEcgi(), ueId)
                                .ifPresent(link -> link.getMeasurements().setSchedMeasReport(
                                        new RnibLink.Measurements.SchedMeasReport(
                                                servCell.getQciVals(),
                                                new RnibLink.Measurements.SchedMeasReport.ResourceUsage(
                                                        servCell.getPrbUsage().getPrbUsageDl(),
                                                        servCell.getPrbUsage().getPrbUsageUl()
                                                ),
                                                new RnibLink.Measurements.SchedMeasReport.Mcs(
                                                        servCell.getMcsDl(),
                                                        servCell.getMcsUl()
                                                ),
                                                new RnibLink.Measurements.SchedMeasReport.NumSchedTtis(
                                                        servCell.getNumSchedTtisDl(),
                                                        servCell.getNumSchedTtisUl()
                                                ),
                                                new RnibLink.Measurements.SchedMeasReport.DlRankStats(
                                                        servCell.getRankDl1(),
                                                        servCell.getRankDl2()
                                                )
                                        )
                                )
                                )
                        )
                );
            });
        }

        /**
         * Handle SchedMeasReportPerCell.
         *
         * @param schedMeasReportPerCell SchedMeasReportPerCell
         */
        private void handleSchedMeasReportPerCell(SchedMeasReportPerCell schedMeasReportPerCell) {
            xranStore.getCell(schedMeasReportPerCell.getEcgi()).ifPresent(cell -> cell.getMeasurements().setPrbUsage(
                    new RnibCell.Measurements.PrbUsage(
                            schedMeasReportPerCell.getQciVals(),
                            schedMeasReportPerCell.getPrbUsagePcell(),
                            schedMeasReportPerCell.getPrbUsageScell()
                    )
            ));
        }

        /**
         * Handle PDCPMeasReportPerUe.
         *
         * @param pdcpMeasReportPerUe PDCPMeasReportPerUe
         */
        private void handlePdcpMeasReportPerUe(PDCPMeasReportPerUe pdcpMeasReportPerUe) {
            xranStore.getUe(pdcpMeasReportPerUe.getEcgi(), pdcpMeasReportPerUe.getCrnti()).ifPresent(ue -> {
                Long ueId = ue.getId();
                xranStore.getLink(pdcpMeasReportPerUe.getEcgi(), ueId).ifPresent(link ->
                        link.getMeasurements().setPdcpMeasReport(
                                new RnibLink.Measurements.PdcpMeasReport(
                                        pdcpMeasReportPerUe.getQciVals(),
                                        new RnibLink.Measurements.PdcpMeasReport.PdcpThroughput(
                                                pdcpMeasReportPerUe.getThroughputDl(),
                                                pdcpMeasReportPerUe.getThroughputUl()
                                        ),
                                        new RnibLink.Measurements.PdcpMeasReport.DataVol(
                                                pdcpMeasReportPerUe.getDataVolDl(),
                                                pdcpMeasReportPerUe.getDataVolUl()
                                        ),
                                        pdcpMeasReportPerUe.getPktDelayDl(),
                                        pdcpMeasReportPerUe.getPktDiscardRateDl(),
                                        new RnibLink.Measurements.PdcpMeasReport.PktLossRate(
                                                pdcpMeasReportPerUe.getPktLossRateDl(),
                                                pdcpMeasReportPerUe.getPktLossRateUl()
                                        )
                                )
                        )
                );
            });
        }

        /**
         * Handle UECapabilityInfo.
         *
         * @param capabilityInfo UECapabilityInfo
         */
        private void handleCapabilityInfo(UECapabilityInfo capabilityInfo) {
            xranStore.getUe(capabilityInfo.getEcgi(), capabilityInfo.getCrnti())
                    .ifPresent(
                            ue -> ue.setCapability(capabilityInfo)
                    );
        }

        /**
         * Handle UECapabilityEnquiry.
         *
         * @param ueCapabilityEnquiry UECapabilityEnquiry
         * @param ctx                 ChannelHandlerContext
         */
        private void handleUecapabilityenquiry(UECapabilityEnquiry ueCapabilityEnquiry, ChannelHandlerContext ctx) {
            XrancPdu xrancPdu = UECapabilityEnquiry.constructPacket(ueCapabilityEnquiry.getEcgi(),
                    ueCapabilityEnquiry.getCrnti());
            ctx.writeAndFlush(getSctpMessage(xrancPdu));
        }

        /**
         * Handle ScellAddStatus.
         *
         * @param scellAddStatus ScellAddStatus
         */
        private void handleScellAddStatus(ScellAddStatus scellAddStatus) {
            xranStore.getUe(scellAddStatus.getEcgi(), scellAddStatus.getCrnti()).ifPresent(ue -> {
                Long ueId = ue.getId();
                try {
                    scellAddMap.get(scellAddStatus.getCrnti()).put("Scell's status: " +
                            scellAddStatus.getStatus());
                    final int[] i = {0};
                    scellAddStatus.getScellsInd().getPCIARFCN().forEach(
                            pciarfcn -> {
                                if (scellAddStatus.getStatus().getBerEnum().get(i[0]).value.intValue() == 0) {
                                    xranStore.getCell(pciarfcn)
                                            .ifPresent(cell -> xranStore.getLink(cell.getEcgi(), ueId)
                                                    .ifPresent(link -> link.setType(RnibLink.Type.SERVING_SECONDARY_CA))
                                            );
                                }
                                i[0]++;
                            }
                    );

                } catch (InterruptedException e) {
                    log.error(ExceptionUtils.getFullStackTrace(e));
                } finally {
                    scellAddMap.remove(scellAddStatus.getCrnti());
                }
            });
        }

        /**
         * Handle RRMConfigStatus.
         *
         * @param rrmConfigStatus RRMConfigStatus
         */
        private void handleRrmConfigStatus(RRMConfigStatus rrmConfigStatus) {
            try {
                rrmCellMap.get(rrmConfigStatus.getEcgi())
                        .put("RRM Config's status: " + rrmConfigStatus.getStatus());
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getFullStackTrace(e));
            } finally {
                rrmCellMap.remove(rrmConfigStatus.getEcgi());
            }
        }

        /**
         * Handle TrafficSplitConfig.
         *
         * @param trafficSplitConfig TrafficSplitConfig
         */
        private void handleTrafficSplitConfig(TrafficSplitConfig trafficSplitConfig) {
            xranStore.getUe(trafficSplitConfig.getEcgi(), trafficSplitConfig.getCrnti()).ifPresent(ue -> {
                Long ueId = ue.getId();
                List<TrafficSplitPercentage> splitPercentages = trafficSplitConfig
                        .getTrafficSplitPercent().getTrafficSplitPercentage();

                splitPercentages.forEach(trafficSplitPercentage -> xranStore.getCell(trafficSplitPercentage.getEcgi())
                        .ifPresent(cell -> xranStore.getLink(cell.getEcgi(), ueId)
                                .ifPresent(link -> link.setTrafficPercent(trafficSplitPercentage))));
            });
        }

        /**
         * Handle context update depending if its handoff or not.
         *
         * @param contextUpdate context update packet
         * @param ctx           channel context for the CELL
         * @param handoff       true if we handle a Hand Off
         */
        private void handlePairedPackets(UEContextUpdate contextUpdate, ChannelHandlerContext ctx, boolean handoff) {
            xranStore.getCell(contextUpdate.getEcgi()).ifPresent(cell -> {
                        Optional<RnibUe> optionalUe;
                        if (handoff) {
                            try {
                                optionalUe = xranStore.getUe(ueIdQueue.take());
                            } catch (InterruptedException e) {
                                log.error(ExceptionUtils.getFullStackTrace(e));
                                optionalUe = Optional.of(new RnibUe());
                            }
                        } else {
                            optionalUe = Optional.of(new RnibUe());
                        }

                        optionalUe.ifPresent(ue -> {
                            ue.getContextIds().setMmeS1apId(contextUpdate.getMMEUES1APID());
                            ue.getContextIds().setEnbS1apId(contextUpdate.getENBUES1APID());
                            ue.setCrnti(contextUpdate.getCrnti());
                            hostAgent.addConnectedHost(ue, cell, ctx);
                        });
                    }
            );
        }
    }

    /**
     * Internal class for NetworkConfigListener.
     */
    class InternalNetworkConfigListener implements NetworkConfigListener {

        @Override
        public void event(NetworkConfigEvent event) {
            switch (event.type()) {
                case CONFIG_REGISTERED:
                    break;
                case CONFIG_UNREGISTERED:
                    break;
                case CONFIG_ADDED:
                case CONFIG_UPDATED:
                    if (event.configClass() == CONFIG_CLASS) {
                        handleConfigEvent(event.config());
                    }
                    break;
                case CONFIG_REMOVED:
                    break;
                default:
                    break;
            }
        }

        /**
         * Handle config event.
         *
         * @param configOptional config
         */
        private void handleConfigEvent(Optional<Config> configOptional) {
            configOptional.ifPresent(config -> {
                xranConfig = (XranConfig) config;
                northboundTimeout = xranConfig.getNorthBoundTimeout();
                legitCells.putAll(xranConfig.activeCellSet());
                xranServer.start(deviceAgent, hostAgent, packetAgent,
                        xranConfig.getXrancIp(), xranConfig.getXrancPort());
            });
        }
    }
}
