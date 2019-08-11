package com.gmail.bishoybasily.k8s.crd;

import com.gmail.bishoybasily.k8s.crd.fidelyo.Fidelyo;
import com.gmail.bishoybasily.k8s.crd.fidelyo.FidelyoDoneable;
import com.gmail.bishoybasily.k8s.crd.fidelyo.FidelyoList;
import com.gmail.bishoybasily.k8s.crd.fidelyo.FidelyoSpec;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionBuilder;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionList;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.log4j.Log4j;

import java.util.List;

@Log4j
public class Main {

    private static String CRD_GROUP = "bishoybasily.gmail.com";
    private static String CRD_NAME = "fidelyos." + CRD_GROUP;
    private static String CRD_SCOPE = "Namespaced";
    private static String CRD_VERSION = "v1";
    private static String CRD_KIND = "Fidelyo";
    private static String CRD_SHORT_NAME = "fd";
    private static String CRD_PLURAL = "fidelyos";

    public static void main(String[] args) throws Exception {

        Main main = new Main();
        main.runTheController();

    }

    private void runTheController() throws InterruptedException {

        try (KubernetesClient client = new DefaultKubernetesClient()) {

            CustomResourceDefinition fidelyoCRD = getOrCreateTheDefinition(client);

            NonNamespaceOperation<Fidelyo, FidelyoList, FidelyoDoneable, Resource<Fidelyo, FidelyoDoneable>> fidelyoClient = buildTheCrdClient(client, fidelyoCRD);

            watchTheResourceChanges(client, fidelyoClient);

            keepTheApplicationRunning();

        }

    }

    private CustomResourceDefinition getOrCreateTheDefinition(KubernetesClient client) {

        CustomResourceDefinitionList crds = client.customResourceDefinitions().list();

        List<CustomResourceDefinition> crdsItems = crds.getItems();

        log.info("Found " + crdsItems.size() + " CRD(s)");

        CustomResourceDefinition fidelyoCRD = null;

        for (CustomResourceDefinition crd : crdsItems) {
            ObjectMeta metadata = crd.getMetadata();
            if (metadata != null) {
                String name = metadata.getName();
                if (CRD_NAME.equals(name)) {
                    fidelyoCRD = crd;

                    log.info("Found CRD: " + fidelyoCRD.getMetadata().getSelfLink());

                }
            }
        }

        if (fidelyoCRD == null) {

            fidelyoCRD = new CustomResourceDefinitionBuilder()
                    // @formatter:off
                    .withApiVersion("apiextensions.k8s.io/v1beta1")
                    .withNewMetadata()
                        .withName(CRD_NAME)
                    .endMetadata()
                    .withNewSpec()
                        .withGroup(CRD_GROUP)
                        .withVersion(CRD_VERSION)
                        .withScope(CRD_SCOPE)
                        .withNewNames()
                            .withKind(CRD_KIND)
                            .withShortNames(CRD_SHORT_NAME)
                            .withPlural(CRD_PLURAL)
                        .endNames()
                    .endSpec()
                    // @formatter:on
                    .build();

            client.customResourceDefinitions().create(fidelyoCRD);

            log.info("Created CRD " + fidelyoCRD.getMetadata().getName());

        }

        return fidelyoCRD;

    }

    private NonNamespaceOperation<Fidelyo, FidelyoList, FidelyoDoneable, Resource<Fidelyo, FidelyoDoneable>> buildTheCrdClient(KubernetesClient client, CustomResourceDefinition fidelyoCRD) {
        return client.customResources(fidelyoCRD, Fidelyo.class, FidelyoList.class, FidelyoDoneable.class).inNamespace(client.getNamespace());
    }

    private void watchTheResourceChanges(KubernetesClient client, NonNamespaceOperation<Fidelyo, FidelyoList, FidelyoDoneable, Resource<Fidelyo, FidelyoDoneable>> fidelyoClient) {
        log.info("Watching fidelyo changes...");
        fidelyoClient.watch(new Watcher<Fidelyo>() {

            @Override
            public void eventReceived(Action action, Fidelyo resource) {

                FidelyoSpec spec = resource.getSpec();
                ObjectMeta metadata = resource.getMetadata();
                String ns = resource.getMetadata().getNamespace();

                if (action.equals(Action.ADDED) || action.equals(Action.MODIFIED)) {

                    // @formatter:off
                    Deployment deployment = new DeploymentBuilder(true)
                            .withNewMetadata()
                                .withNamespace(metadata.getNamespace())
                                .withName(metadata.getName()).withNamespace(ns)
                            .endMetadata()
                            .withNewSpec()
                                .withReplicas(spec.getReplicas())
                                .withNewTemplate()
                                    .withNewMetadata()
                                        .withNamespace(metadata.getNamespace())
                                        .addToLabels("app", metadata.getName())
                                    .endMetadata()
                                    .withNewSpec()
                                        .addNewContainer()
                                            .withName(metadata.getName())
                                            .withImage(spec.getImage() + ":" + spec.getVersion())
                                            .addNewPort()
                                                .withContainerPort(spec.getPort())
                                            .endPort()
                                            .addAllToEnv(spec.getEnvVars())
                                        .endContainer()
                                    .endSpec()
                                .endTemplate()
                                .withNewSelector()
                                    .addToMatchLabels("app", metadata.getName())
                                .endSelector()
                            .endSpec()
                            .build();
                            // @formatter:on

                    deployment = client.extensions().deployments().inNamespace(ns).createOrReplace(deployment);

                    // @formatter:off
                    Service service = new ServiceBuilder(true)
                            .withNewMetadata()
                                .withNamespace(metadata.getNamespace())
                                .withName(metadata.getName() ).withNamespace(ns)
                            .endMetadata()
                            .withNewSpec()
                                .addToSelector("app", metadata.getName() )
                                .withType(spec.getServiceType())
                                .addNewPort()
                                    .withNewTargetPort(spec.getPort())
                                    .withPort(spec.getPort())
                                    .withName("default")
                                    .withProtocol("TCP")
                                .endPort()
                            .endSpec()
                            .build();
                            // @formatter:on

                    service = client.services().inNamespace(ns).createOrReplace(service);

                    if (action.equals(Action.ADDED))
                        fidelyoClient.createOrReplace(resource.setSpec(spec.setDeployment(deployment).setService(service)));

                }

                if (action.equals(Action.DELETED)) {
                    client.extensions().deployments().inNamespace(ns).delete(resource.getSpec().getDeployment());
                    client.services().inNamespace(ns).delete(resource.getSpec().getService());
                }

                log.info("==> " + action + " for " + resource);

            }

            @Override
            public void onClose(KubernetesClientException cause) {

            }

        });
    }

    private void keepTheApplicationRunning() throws InterruptedException {
        log.info("Press Ctrl+C to shutdown application");
        Thread.currentThread().join();
    }
}
