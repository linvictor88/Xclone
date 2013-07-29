
package com.vmware.vim25;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DynamicData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DynamicData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dynamicType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dynamicProperty" type="{urn:vim25}DynamicProperty" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DynamicData", propOrder = {
    "dynamicType",
    "dynamicProperty"
})
@XmlSeeAlso({
    ClusterEnterMaintenanceResult.class,
    DistributedVirtualSwitchManagerCompatibilityResult.class,
    UserSession.class,
    HostImageProfileSummary.class,
    IscsiMigrationDependency.class,
    ProfileDescription.class,
    PhysicalNicHintInfo.class,
    HostVMotionCompatibility.class,
    VirtualMachineConfigOptionDescriptor.class,
    HostDateTimeSystemTimeZone.class,
    LicenseUsageInfo.class,
    HostServiceTicket.class,
    KernelModuleInfo.class,
    VirtualNicManagerNetConfig.class,
    HostUnresolvedVmfsVolume.class,
    LicenseManagerLicenseInfo.class,
    LocalizedMethodFault.class,
    ServiceContent.class,
    OvfValidateHostResult.class,
    AlarmState.class,
    PerfProviderSummary.class,
    EventArgDesc.class,
    VirtualMachineConfigOption.class,
    DiagnosticManagerLogDescriptor.class,
    ResourceConfigOption.class,
    RetrieveResult.class,
    VirtualMachineMksTicket.class,
    UpdateSet.class,
    VmfsDatastoreOption.class,
    StoragePlacementResult.class,
    HostDiskDimensionsChs.class,
    ProfileProfileStructure.class,
    AnswerFile.class,
    HostStorageArrayTypePolicyOption.class,
    Permission.class,
    IscsiStatus.class,
    OvfParseDescriptorResult.class,
    HostUnresolvedVmfsResolutionResult.class,
    HostDiskPartitionInfo.class,
    ProductComponentInfo.class,
    GuestListFileInfo.class,
    DistributedVirtualSwitchProductSpec.class,
    CustomizationSpecItem.class,
    IscsiPortInfo.class,
    FileTransferInformation.class,
    DatabaseSizeEstimate.class,
    IpPool.class,
    HostBootDeviceInfo.class,
    CustomFieldDef.class,
    SessionManagerLocalTicket.class,
    OvfCreateImportSpecResult.class,
    ProfileExpressionMetadata.class,
    TaskInfo.class,
    HostDiagnosticPartition.class,
    HttpNfcLeaseManifestEntry.class,
    ProfileMetadata.class,
    HostConnectInfo.class,
    ComplianceResult.class,
    LicenseFeatureInfo.class,
    ClusterHostRecommendation.class,
    HostPathSelectionPolicyOption.class,
    DistributedVirtualPort.class,
    OvfCreateDescriptorResult.class,
    LicenseAvailabilityInfo.class,
    PerfCompositeMetric.class,
    DiskChangeInfo.class,
    LicenseAssignmentManagerLicenseAssignment.class,
    DistributedVirtualSwitchHostProductSpec.class,
    SessionManagerGenericServiceTicket.class,
    AnswerFileStatusResult.class,
    HostDiagnosticPartitionCreateDescription.class,
    HostProfileManagerConfigTaskList.class,
    ProfilePolicyMetadata.class,
    PerfCounterInfo.class,
    GuestProcessInfo.class,
    VirtualMachineTicket.class,
    PerfMetricId.class,
    HostCapability.class,
    DiagnosticManagerLogHeader.class,
    ConfigTarget.class,
    ObjectContent.class,
    HostNetworkConfigResult.class,
    DVSManagerDvsConfigTarget.class,
    StorageIORMConfigOption.class,
    Extension.class,
    ProfileExecuteResult.class,
    HostDiagnosticPartitionCreateOption.class,
    VAppOvfSectionInfo.class,
    HealthSystemRuntime.class,
    ResourceConfigSpec.class,
    FcoeConfigVlanRange.class,
    HostDiskMappingInfo.class,
    DVPortState.class,
    HostDatastoreBrowserSearchSpec.class,
    VirtualMachineConfigSpec.class,
    DistributedVirtualSwitchManagerHostContainer.class,
    NumericRange.class,
    HostVmfsRescanResult.class,
    ClusterDrsMigration.class,
    UpdateVirtualMachineFilesResult.class,
    TaskFilterSpec.class,
    NetIpConfigInfoIpAddress.class,
    ClusterActionHistory.class,
    VirtualMachineFlagInfo.class,
    PerformanceDescription.class,
    PropertyChange.class,
    HttpNfcLeaseDatastoreLeaseInfo.class,
    HostPciPassthruConfig.class,
    HostFirewallRule.class,
    HostCacheConfigurationInfo.class,
    WaitOptions.class,
    PodDiskLocator.class,
    HostFlagInfo.class,
    HostPortGroupConfig.class,
    ComplianceProfile.class,
    StorageIOAllocationOption.class,
    DistributedVirtualSwitchPortConnection.class,
    HostSnmpDestination.class,
    AlarmSetting.class,
    InventoryDescription.class,
    HostFirewallRulesetIpList.class,
    VmConfigFileQueryFilter.class,
    DistributedVirtualSwitchHostMemberRuntimeState.class,
    ComputeResourceHostSPBMLicenseInfo.class,
    HostVirtualSwitch.class,
    HostNumaNode.class,
    HostPciPassthruInfo.class,
    StorageDrsVmConfigInfo.class,
    SessionManagerServiceRequestSpec.class,
    HostDiskMappingPartitionOption.class,
    HostFirewallInfo.class,
    ProfileProfileStructureProperty.class,
    DiagnosticManagerBundleInfo.class,
    UserSearchResult.class,
    PhysicalNicSpec.class,
    HostMountInfo.class,
    VirtualMachineRuntimeInfoDasProtectionState.class,
    StorageDrsPodConfigSpec.class,
    VirtualAppLinkInfo.class,
    VirtualMachineConfigInfoDatastoreUrlPair.class,
    DVSFeatureCapability.class,
    HostDateTimeInfo.class,
    CustomizationUserData.class,
    CustomizationOptions.class,
    LicenseReservationInfo.class,
    VirtualMachineFileInfo.class,
    HostVMotionNetConfig.class,
    AutoStartDefaults.class,
    DVPortgroupConfigSpec.class,
    FcoeConfig.class,
    DasHeartbeatDatastoreInfo.class,
    ClusterDasVmConfigInfo.class,
    HostFileSystemVolumeInfo.class,
    HostNicTeamingPolicy.class,
    GuestDiskInfo.class,
    HostDhcpServiceSpec.class,
    HostEsxAgentHostManagerConfigInfo.class,
    CustomizationIPSettingsIpV6AddressSpec.class,
    OvfConsumerOstNode.class,
    VAppPropertyInfo.class,
    StorageIORMInfo.class,
    VirtualMachineFileLayoutExDiskUnit.class,
    ClusterDrsRecommendation.class,
    LocalizableMessage.class,
    CustomizationSpec.class,
    HostNtpConfig.class,
    EventDescription.class,
    PropertyFilterSpec.class,
    VirtualMachineIdeDiskDevicePartitionInfo.class,
    VirtualMachineRelocateSpec.class,
    HostInternetScsiHbaDigestProperties.class,
    HostConfigSummary.class,
    HostMultipathStateInfoPath.class,
    DistributedVirtualSwitchHostMemberConfigSpec.class,
    CustomizationGuiRunOnce.class,
    HttpNfcLeaseInfo.class,
    DVPortConfigSpec.class,
    KeyValue.class,
    LicenseManagerEvaluationInfo.class,
    HostVmciAccessManagerAccessSpec.class,
    AutoStartPowerInfo.class,
    CheckResult.class,
    HostFileAccess.class,
    NetIpRouteConfigInfoIpRoute.class,
    HostNumericSensorInfo.class,
    HostVirtualNicConnection.class,
    VirtualMachineFileLayoutExSnapshotLayout.class,
    ProfileMetadataProfileSortSpec.class,
    HostPciDevice.class,
    VirtualMachineQuestionInfo.class,
    HostPatchManagerStatus.class,
    HostInternetScsiHbaAuthenticationCapabilities.class,
    HostScsiTopologyInterface.class,
    HostVirtualSwitchBeaconConfig.class,
    ExtendedEventPair.class,
    HostConnectSpec.class,
    AnswerFileUpdateFailure.class,
    NetDhcpConfigInfoDhcpOptions.class,
    HostActiveDirectory.class,
    AuthorizationPrivilege.class,
    VAppIPAssignmentInfo.class,
    HostScsiDiskPartition.class,
    HostVirtualSwitchSpec.class,
    HostLowLevelProvisioningManagerSnapshotLayoutSpec.class,
    MissingProperty.class,
    HostProxySwitch.class,
    HostCpuPackage.class,
    DatastoreOption.class,
    ClusterConfigInfo.class,
    ResourceAllocationOption.class,
    HostPlugStoreTopology.class,
    StorageIORMConfigSpec.class,
    HostDiskPartitionBlockRange.class,
    HostUnresolvedVmfsVolumeResolveStatus.class,
    LinkLayerDiscoveryProtocolInfo.class,
    VirtualMachineStorageSummary.class,
    VirtualMachineFileLayout.class,
    VAppEntityConfigInfo.class,
    TaskFilterSpecByEntity.class,
    HostNetworkConfig.class,
    SelectionSpec.class,
    AnswerFileCreateSpec.class,
    PodStorageDrsEntry.class,
    PhysicalNicConfig.class,
    HostHardwareSummary.class,
    HostLicenseSpec.class,
    TaskFilterSpecByTime.class,
    ClusterDrsFaults.class,
    HostResignatureRescanResult.class,
    DVPortConfigInfo.class,
    HostPatchManagerResult.class,
    HostFileSystemMountInfo.class,
    VirtualDiskId.class,
    HostLocalFileSystemVolumeSpec.class,
    ClusterDasConfigInfo.class,
    HostPowerPolicy.class,
    HostInternetScsiHbaTargetSet.class,
    DatabaseSizeParam.class,
    HostConfigChange.class,
    HostInternetScsiHbaSendTarget.class,
    DVSCapability.class,
    HostIpRouteOp.class,
    ProfileDescriptionSection.class,
    VirtualMachineConsolePreferences.class,
    HostFirewallConfigRuleSetConfig.class,
    VmConfigInfo.class,
    DVPortStatus.class,
    ExtensionPrivilegeInfo.class,
    EventDescriptionEventDetail.class,
    NetIpRouteConfigInfo.class,
    VirtualMachineSnapshotInfo.class,
    NetBIOSConfigInfo.class,
    VmPodConfigForPlacement.class,
    DVSNetworkResourcePoolConfigSpec.class,
    DistributedVirtualSwitchHostMemberConfigInfo.class,
    StorageRequirement.class,
    ExtensionServerInfo.class,
    VirtualHardware.class,
    HttpNfcLeaseDeviceUrl.class,
    EnumDescription.class,
    ExtensionResourceInfo.class,
    AboutInfo.class,
    PhysicalNic.class,
    HttpNfcLeaseHostInfo.class,
    HostNetworkPolicy.class,
    PropertyFilterUpdate.class,
    CustomizationLicenseFilePrintData.class,
    DVSSummary.class,
    DistributedVirtualSwitchPortConnectee.class,
    HostServiceConfig.class,
    HostDhcpService.class,
    StoragePlacementSpec.class,
    NetIpRouteConfigSpecIpRouteSpec.class,
    HostPortGroupSpec.class,
    PerfSampleInfo.class,
    ClusterDasAamNodeState.class,
    DistributedVirtualSwitchPortCriteria.class,
    GuestNicInfo.class,
    HostSystemResourceInfo.class,
    HostSslThumbprintInfo.class,
    HostDiskDimensions.class,
    HostPlugStoreTopologyPath.class,
    AuthorizationRole.class,
    FileQueryFlags.class,
    HostDevice.class,
    StorageDrsPodConfigInfo.class,
    ClusterRecommendation.class,
    HostVMotionInfo.class,
    ProfilePropertyPath.class,
    HostPlugStoreTopologyTarget.class,
    ScsiLunDescriptor.class,
    ScsiLunDurableName.class,
    ClusterNotAttemptedVmInfo.class,
    OvfFile.class,
    ExtensionHealthInfo.class,
    HostDiskPartitionLayout.class,
    EventFilterSpecByTime.class,
    VirtualMachineRelocateSpecDiskLocator.class,
    GuestInfo.class,
    ModeInfo.class,
    ScheduledTaskDescription.class,
    FcoeConfigFcoeCapabilities.class,
    ClusterVmToolsMonitoringSettings.class,
    ExtExtendedProductInfo.class,
    StorageIOAllocationInfo.class,
    HostPlugStoreTopologyDevice.class,
    HostNicOrderPolicy.class,
    OvfNetworkInfo.class,
    OvfCreateDescriptorParams.class,
    HostDiskPartitionAttributes.class,
    ProfilePolicy.class,
    HostFirewallRulesetIpNetwork.class,
    ObjectSpec.class,
    ReplicationInfoDiskSettings.class,
    AlarmDescription.class,
    HostIpConfig.class,
    HostFirewallRulesetRulesetSpec.class,
    DatastoreMountPathDatastorePair.class,
    VMwareVspanPort.class,
    ClusterPowerOnVmResult.class,
    HostMultipathInfoLogicalUnitPolicy.class,
    PhysicalNicLinkInfo.class,
    NetIpRouteConfigInfoGateway.class,
    ComplianceFailure.class,
    OvfNetworkMapping.class,
    PerfQuerySpec.class,
    PhysicalNicHint.class,
    VirtualMachineLegacyNetworkSwitchInfo.class,
    ExtSolutionManagerInfoTabInfo.class,
    CustomizationIPSettings.class,
    HostFirewallDefaultPolicy.class,
    ClusterDasFailoverLevelAdvancedRuntimeInfoHostSlots.class,
    TaskFilterSpecByUsername.class,
    FaultToleranceConfigInfo.class,
    HostIpmiInfo.class,
    HostMultipathInfoLogicalUnitStorageArrayTypePolicy.class,
    ClusterDasAdvancedRuntimeInfo.class,
    VirtualMachineCloneSpec.class,
    DistributedVirtualPortgroupInfo.class,
    ComplianceLocator.class,
    VirtualDeviceConnectOption.class,
    PasswordField.class,
    HostInternetScsiHbaDiscoveryProperties.class,
    OptionValue.class,
    HostStorageOperationalInfo.class,
    HostNetworkTrafficShapingPolicy.class,
    ComputeResourceConfigInfo.class,
    PerformanceManagerCounterLevelMapping.class,
    OvfDeploymentOption.class,
    PowerSystemCapability.class,
    CustomizationSpecInfo.class,
    NetIpRouteConfigSpec.class,
    HostRuntimeInfo.class,
    VmConfigSpec.class,
    HostScsiTopologyTarget.class,
    VirtualMachineUsageOnDatastore.class,
    DVSNetworkResourceManagementCapability.class,
    NetIpRouteConfigSpecGatewaySpec.class,
    HostService.class,
    ExtManagedEntityInfo.class,
    HostMultipathStateInfo.class,
    ReplicationVmProgressInfo.class,
    VirtualMachineFileLayoutSnapshotLayout.class,
    HostSystemIdentificationInfo.class,
    KernelModuleSectionInfo.class,
    HostNetworkSecurityPolicy.class,
    NetIpConfigInfo.class,
    DVSNetworkResourcePoolAllocationInfo.class,
    ClusterDasFdmHostState.class,
    StorageDrsIoLoadBalanceConfig.class,
    LicenseDiagnostics.class,
    EventFilterSpecByUsername.class,
    StorageDrsSpaceLoadBalanceConfig.class,
    HostVirtualNicConfig.class,
    DistributedVirtualSwitchHostMember.class,
    HostSystemInfo.class,
    NetDhcpConfigSpec.class,
    VirtualMachineDefaultPowerOpInfo.class,
    HostForceMountedInfo.class,
    HostDatastoreSystemCapabilities.class,
    PhysicalNicCdpInfo.class,
    HostIpRouteEntry.class,
    LinkDiscoveryProtocolConfig.class,
    HostInternetScsiHbaIPCapabilities.class,
    HostPatchManagerLocator.class,
    DistributedVirtualSwitchInfo.class,
    OvfResourceMap.class,
    ClusterDasData.class,
    StorageDrsPodSelectionSpec.class,
    HostMultipathInfoLogicalUnit.class,
    VMwareIpfixConfig.class,
    IpPoolAssociation.class,
    HostConnectInfoNetworkInfo.class,
    VnicPortArgument.class,
    Tag.class,
    PerformanceStatisticsDescription.class,
    HostSecuritySpec.class,
    AuthorizationDescription.class,
    HostVirtualNic.class,
    HostBootDevice.class,
    HostProxySwitchSpec.class,
    AlarmSpec.class,
    ResourcePoolQuickStats.class,
    CustomizationIdentitySettings.class,
    ProfileUpdateFailedUpdateFailure.class,
    OvfManagerCommonParams.class,
    HostActiveDirectorySpec.class,
    ProfileExecuteError.class,
    ClusterDrsVmConfigInfo.class,
    FaultToleranceSecondaryOpResult.class,
    ProfilePolicyOptionMetadata.class,
    CustomizationIdentification.class,
    DVSCreateSpec.class,
    HostPatchManagerStatusPrerequisitePatch.class,
    UpdateVirtualMachineFilesResultFailedVmFileInfo.class,
    MethodActionArgument.class,
    GuestOsDescriptor.class,
    ClusterDrsFaultsFaultsByVm.class,
    HostBIOSInfo.class,
    HostScsiTopology.class,
    ClusterFailoverHostAdmissionControlInfoHostStatus.class,
    VirtualMachineSnapshotTree.class,
    HostDiskDimensionsLba.class,
    VirtualMachineFileLayoutExFileInfo.class,
    VAppProductInfo.class,
    ClusterDasAdmissionControlInfo.class,
    NetDnsConfigInfo.class,
    SharesInfo.class,
    HostDiskMappingPartitionInfo.class,
    HostPortGroup.class,
    NetIpStackInfo.class,
    DVPortgroupConfigInfo.class,
    HostInternetScsiHbaAuthenticationProperties.class,
    HostUnresolvedVmfsExtent.class,
    PropertySpec.class,
    VirtualMachineMemoryReservationSpec.class,
    HostNatServiceConfig.class,
    VirtualMachineGuestSummary.class,
    HostVirtualNicSpec.class,
    OvfFileItem.class,
    HostIpRouteConfig.class,
    NetIpConfigSpecIpAddressSpec.class,
    HostSystemHealthInfo.class,
    VirtualDeviceConnectInfo.class,
    HostDnsConfig.class,
    DistributedVirtualSwitchKeyedOpaqueBlob.class,
    VirtualMachineFileLayoutEx.class,
    VAppCloneSpecResourceMap.class,
    CustomizationPassword.class,
    HostMultipathInfoPath.class,
    HostNumaInfo.class,
    VmConfigFileQueryFlags.class,
    NetIpConfigSpec.class,
    MultipleCertificatesVerifyFaultThumbprintData.class,
    SharesOption.class,
    ClusterConfigSpec.class,
    HostDatastoreBrowserSearchResults.class,
    HostCpuIdInfo.class,
    HostLowLevelProvisioningManagerDiskLayoutSpec.class,
    ProfileDeferredPolicyOptionParameter.class,
    VirtualMachineNetworkShaperInfo.class,
    HostPatchManagerPatchManagerOperationSpec.class,
    ClusterDasVmSettings.class,
    ClusterGroupInfo.class,
    HostListSummaryQuickStats.class,
    ClusterDpmConfigInfo.class,
    HostConfigManager.class,
    HostSystemReconnectSpec.class,
    ToolsConfigInfo.class,
    HostLicenseConnectInfo.class,
    HostFirewallRuleset.class,
    PolicyOption.class,
    VirtualMachineFileLayoutDiskLayout.class,
    DistributedVirtualSwitchHostMemberPnicSpec.class,
    ArrayUpdateSpec.class,
    HostNatServiceSpec.class,
    ClusterDasAdmissionControlPolicy.class,
    HostNetCapabilities.class,
    NetIpStackInfoDefaultRouter.class,
    DatacenterMismatchArgument.class,
    TaskDescription.class,
    HostNatService.class,
    HostIpConfigIpV6Address.class,
    DatastoreHostMount.class,
    HostSnmpConfigSpec.class,
    ScheduledTaskSpec.class,
    HostCpuInfo.class,
    HostVMotionConfig.class,
    HostIpRouteTableConfig.class,
    NetDhcpConfigInfo.class,
    VirtualMachineDeviceRuntimeInfoDeviceRuntimeState.class,
    HostConfigSpec.class,
    HostInternetScsiHbaDiscoveryCapabilities.class,
    CustomizationGuiUnattended.class,
    ClusterDasFailoverLevelAdvancedRuntimeInfoSlotInfo.class,
    HostStorageDeviceInfo.class,
    FcoeConfigFcoeSpecification.class,
    VirtualMachineRuntimeInfo.class,
    RetrieveOptions.class,
    VirtualMachineMemoryReservationInfo.class,
    StoragePodSummary.class,
    VMwareDVSPvlanConfigSpec.class,
    HostPlugStoreTopologyAdapter.class,
    CustomizationAdapterMapping.class,
    HostVirtualSwitchConfig.class,
    HostNatServiceNameServiceSpec.class,
    ComputeResourceSummary.class,
    HostDhcpServiceConfig.class,
    VmDiskFileQueryFlags.class,
    ServiceConsoleReservationInfo.class,
    ClusterDasHostInfo.class,
    NetIpStackInfoNetToMedia.class,
    VirtualMachineFileLayoutExDiskLayout.class,
    GuestProgramSpec.class,
    ToolsConfigInfoToolsLastInstallInfo.class,
    ProfileCreateSpec.class,
    GuestFileAttributes.class,
    HostFileSystemVolume.class,
    ClusterDrsConfigInfo.class,
    HostLicensableResourceInfo.class,
    HostNetworkInfo.class,
    HostDiagnosticPartitionCreateSpec.class,
    DVSConfigSpec.class,
    TaskReason.class,
    HostHardwareStatusInfo.class,
    HostInternetScsiHbaDigestCapabilities.class,
    VirtualMachineQuickStats.class,
    IscsiDependencyEntity.class,
    ClusterDasHostRecommendation.class,
    VirtualMachineBootOptions.class,
    HostServiceInfo.class,
    VAppCloneSpecNetworkMappingPair.class,
    HostHardwareInfo.class,
    PrivilegePolicyDef.class,
    HostIpRouteTableInfo.class,
    VirtualMachineConfigInfo.class,
    ClusterDpmHostConfigInfo.class,
    HostDatastoreConnectInfo.class,
    HostIpConfigIpV6AddressConfiguration.class,
    OptionType.class,
    EventFilterSpec.class,
    LicenseSource.class,
    ProfileParameterMetadata.class,
    EventAlarmExpressionComparison.class,
    VirtualMachineSummary.class,
    ProfileExpression.class,
    OvfConsumerOvfSection.class,
    DatastoreSummary.class,
    VMwareDVSVspanConfigSpec.class,
    HostVirtualNicManagerNicTypeSelection.class,
    HostFibreChannelOverEthernetHbaLinkInfo.class,
    ResourcePoolSummary.class,
    PerfMetricSeries.class,
    VirtualDeviceConfigSpec.class,
    DvsOutOfSyncHostArgument.class,
    GuestAuthentication.class,
    VirtualDiskSpec.class,
    VMwareDVSPvlanMapEntry.class,
    StorageDrsConfigSpec.class,
    PhysicalNicCdpDeviceCapability.class,
    AlarmTriggeringActionTransitionSpec.class,
    HostVirtualSwitchBridge.class,
    CustomizationGlobalIPSettings.class,
    HostListSummary.class,
    CustomizationName.class,
    HostDiskPartitionSpec.class,
    HostTargetTransport.class,
    VirtualMachineDisplayTopology.class,
    DVSUplinkPortPolicy.class,
    HostNasVolumeSpec.class,
    HostConfigInfo.class,
    IpPoolIpPoolConfigInfo.class,
    ExtensionTaskTypeInfo.class,
    HostFeatureVersionInfo.class,
    DVSConfigInfo.class,
    DistributedVirtualSwitchHostMemberBacking.class,
    NetDnsConfigSpec.class,
    HostCacheConfigurationSpec.class,
    ApplyStorageRecommendationResult.class,
    HostNetOffloadCapabilities.class,
    GuestScreenInfo.class,
    DistributedVirtualSwitchManagerDvsProductSpec.class,
    ExtensionEventTypeInfo.class,
    VirtualMachineCapability.class,
    DatastoreInfo.class,
    VirtualMachineBootOptionsBootableDevice.class,
    CustomFieldValue.class,
    ComputeResourceConfigSpec.class,
    HostPortGroupPort.class,
    VirtualMachineConfigSummary.class,
    NetworkSummary.class,
    HostUnresolvedVmfsResignatureSpec.class,
    VMwareVspanSession.class,
    HostVmfsSpec.class,
    HostUnresolvedVmfsResolutionSpec.class,
    HostPlugStoreTopologyPlugin.class,
    ProfileConfigInfo.class,
    VirtualMachineStorageInfo.class,
    HostHyperThreadScheduleInfo.class,
    ManagedByInfo.class,
    GuestFileInfo.class,
    DVPortgroupPolicy.class,
    KeyAnyValue.class,
    NetDhcpConfigSpecDhcpOptionsSpec.class,
    ExtensionOvfConsumerInfo.class,
    DistributedVirtualSwitchPortStatistics.class,
    GuestStackInfo.class,
    DVSNetworkResourcePool.class,
    DistributedVirtualSwitchManagerHostDvsFilterSpec.class,
    DVSContactInfo.class,
    InheritablePolicy.class,
    HostVirtualNicManagerInfo.class,
    HostHardwareElementInfo.class,
    HostNasVolumeConfig.class,
    HostHostBusAdapter.class,
    HostAuthenticationStoreInfo.class,
    Action.class,
    HostAuthenticationManagerInfo.class,
    StorageDrsConfigInfo.class,
    ExtensionClientInfo.class,
    ProfileApplyProfileProperty.class,
    ClusterAttemptedVmInfo.class,
    VirtualHardwareOption.class,
    ResourcePoolRuntimeInfo.class,
    MissingObject.class,
    CustomizationIpGenerator.class,
    HostDateTimeConfig.class,
    Capability.class,
    HostNicFailureCriteria.class,
    HostServiceSourcePackage.class,
    ResourceAllocationInfo.class,
    PerfInterval.class,
    HostScsiTopologyLun.class,
    VmfsDatastoreBaseOption.class,
    HostMemorySpec.class,
    ObjectUpdate.class,
    DiskChangeExtent.class,
    ImportSpec.class,
    HostDigestInfo.class,
    CustomizationIpV6Generator.class,
    ClusterAction.class,
    PerfEntityMetricBase.class,
    PowerSystemInfo.class,
    HostAccountSpec.class,
    VmDiskFileQueryFilter.class,
    VirtualMachineTargetInfo.class,
    DVPortSetting.class,
    ScsiLunCapabilities.class,
    VirtualDeviceOption.class,
    AlarmAction.class,
    VAppCloneSpec.class,
    DVSPolicy.class,
    HostAutoStartManagerConfig.class,
    HostDiskMappingOption.class,
    AnswerFileStatusError.class,
    DvsOperationBulkFaultFaultOnHost.class,
    HostInternetScsiHbaIPProperties.class,
    FileQuery.class,
    HostSnmpSystemAgentLimits.class,
    VirtualDeviceBackingInfo.class,
    EventFilterSpecByEntity.class,
    VirtualMachineDatastoreVolumeOption.class,
    TaskScheduler.class,
    VirtualMachineDeviceRuntimeInfo.class,
    HostFirewallConfig.class,
    HbrManagerReplicationVmInfo.class,
    VirtualMachineMessage.class,
    HostNatServicePortForwardSpec.class,
    HostProxySwitchConfig.class,
    VirtualDeviceBackingOption.class,
    VirtualDevice.class,
    ExtSolutionManagerInfo.class,
    ResourcePoolResourceUsage.class,
    VirtualMachineAffinityInfo.class,
    ClusterRuleInfo.class,
    AlarmExpression.class,
    HostMultipathInfo.class,
    FileInfo.class,
    VmfsDatastoreSpec.class,
    EventArgument.class,
    DatastoreCapability.class,
    ExtensionFaultTypeInfo.class,
    VirtualMachineConfigInfoOverheadInfo.class,
    LocalizationManagerMessageCatalog.class,
    HostInternetScsiHbaStaticTarget.class,
    ApplyProfile.class,
    Description.class,
    Event.class,
    HostCpuPowerManagementInfo.class
})
public class DynamicData {

    protected String dynamicType;
    protected List<DynamicProperty> dynamicProperty;

    /**
     * Gets the value of the dynamicType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDynamicType() {
        return dynamicType;
    }

    /**
     * Sets the value of the dynamicType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDynamicType(String value) {
        this.dynamicType = value;
    }

    /**
     * Gets the value of the dynamicProperty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dynamicProperty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDynamicProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DynamicProperty }
     * 
     * 
     */
    public List<DynamicProperty> getDynamicProperty() {
        if (dynamicProperty == null) {
            dynamicProperty = new ArrayList<DynamicProperty>();
        }
        return this.dynamicProperty;
    }

}
