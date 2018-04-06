package com.genericgraph.api;

import spock.lang.Specification
import com.genericgraph.api.domain.*;

class GenericQuerySpec extends Specification {

    def "Can build query with labels" () {
        when:
        String result = new GenericQuery.Builder(new GenericQuery(labels:['furry', 'otaku'])).build()

        then:
        result == 'MATCH (n) WHERE n:furry:otaku RETURN n'
    }

    def "Can build query with values" () {
        when:
        String result = new GenericQuery.Builder(new GenericQuery(nodes:[new QueryParameter('name','=','zach')])).build()

        then:
        result == 'MATCH (n) WHERE n.name = "zach" RETURN n'
    }

    def "Can build query with relationship" () {
        when:
        String result = new GenericQuery.Builder(new GenericQuery(relationships:[new GenericQueryRelationship(label:'knows', parameters:[new QueryParameter('years','>',9)])])).build()

        then:
        result == "MATCH ()-[n:knows]-() WHERE n.years > 9 RETURN n"
    }

    def "Can build query with directed relationship" () {
        when:
        String result = new GenericQuery.Builder(new GenericQuery(
            relationships:
                [new GenericQueryRelationship(
                    fromNode: new GenericQueryNode(labels:['coolguy'], parameters:[new QueryParameter('name','=','zach')]),
                    toNode: new GenericQueryNode(labels:['astronaut'], parameters:[new QueryParameter('name','=','jim')]),
                    label:'knows', 
                    parameters:[new QueryParameter('years','>',9)])
                ])
            ).build()

        then:
        result == 'MATCH (nl:coolguy)-[n:knows]-(nr:astronaut) WHERE nl.name = "zach" AND n.years > 9 AND nr.name = "jim" RETURN n'
    }
}