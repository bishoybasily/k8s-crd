package com.gmail.bishoybasily.k8s.crd.fidelyo;

import io.fabric8.kubernetes.client.CustomResourceList;

// the list, which will be returned for operations like get,
// it contains a method called get list will should return an iterable of the Fidelyo type available in the cluster,
// it also holds some extra information about the list itself
public class FidelyoList extends CustomResourceList<Fidelyo> {

}
