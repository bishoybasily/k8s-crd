package com.gmail.bishoybasily.k8s.crd.fidelyo;

import io.fabric8.kubernetes.client.CustomResourceList;

// the list, which will be returned as a result for operations like get fidelyos,
// it contains a method called getList which should return an iterable of the Fidelyos available in the cluster,
// it also holds some extra information about the list itself
public class FidelyoList extends CustomResourceList<Fidelyo> {

}
