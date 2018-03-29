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
        result == "MATCH (n) WHERE n.name = 'zach' RETURN n"
    }

    def "Can build query with relationships" () {
        when:
        String result = new GenericQuery.Builder(new GenericQuery(relationships:[new QueryParameter('years','>',9)])).build()

        then:
        result == "MATCH ()- WHERE n.years > 9 RETURN n"
    }
}