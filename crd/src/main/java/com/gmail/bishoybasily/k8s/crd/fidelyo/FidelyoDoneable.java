package com.gmail.bishoybasily.k8s.crd.fidelyo;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

// the doneable, which will be returned as a result for the create operation for example,
// doneable contains a done method that will return the Fidelyo item after applying it to the cluster
public class FidelyoDoneable extends CustomResourceDoneable<Fidelyo> {

    public FidelyoDoneable(Fidelyo resource, Function function) {
        super(resource, function);
    }

}
