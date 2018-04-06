package com.genericgraph.api;

import spock.lang.Specification
import com.genericgraph.api.domain.*;

class GenericQueryNodeSpec extends Specification {
    GenericQueryNode genericQueryNode = new GenericQueryNode(labels:['married', 'DVM'], parameters:[new QueryParameter('age','<',50)])

    def "can get labels" () {
        when:
        String labels = new GenericQueryNode.Builder(genericQueryNode, 'nl').buildLabels()

        then:
        labels == 'nl:married:DVM'
    }

    def "can get parameters" () {
        when:
        String wherePart = new GenericQueryNode.Builder(genericQueryNode, 'nl').buildWhere()

        then:
        wherePart == 'nl.age < 50 AND '
    }
}