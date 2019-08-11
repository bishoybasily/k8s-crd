package com.gmail.bishoybasily.k8s.crd.fidelyo;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

public class FidelyoDoneable extends CustomResourceDoneable<Fidelyo> {

    public FidelyoDoneable(Fidelyo resource, Function function) {
        super(resource, function);
    }

}
