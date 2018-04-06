package com.genericgraph.api;

import spock.lang.Specification
import com.genericgraph.api.domain.*;

class GenericQueryRelationshipSpec extends Specification {
    GenericQueryRelationship relationship

    def "labels are writen to label query part" () {
        given:
        relationship = new GenericQueryRelationship(id:'n', label:'knows')

        when:
        String labels = new GenericQueryRelationship.Builder(relationship).buildLabel()
        
        then:
        labels == '()-[n:knows]-()'
    }

    def "labes are written to label query part with to and from nodes" () {
        given:
        relationship = new GenericQueryRelationship(id:'n', label:'knows', fromNode: new GenericQueryNode(labels:['doctor']), toNode: new GenericQueryNode(labels:['DVM']))

        when:
        String labels = new GenericQueryRelationship.Builder(relationship).buildLabel()
        
        then:
        labels == '(nf:doctor)-[n:knows]-(nt:DVM)'
    }

    def "query paramaters are writen to where query part" () {
        given:
        relationship = new GenericQueryRelationship(id:'n', parameters:[new QueryParameter('year','>',9), new QueryParameter('from','=','WV')])

        when:
        String labels = new GenericQueryRelationship.Builder(relationship).buildWhere()
        
        then:
        labels == 'n.year > 9 AND n.from = "WV" AND '
    }

}