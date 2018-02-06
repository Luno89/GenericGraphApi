package com.genericgraph.api;

import spock.lang.Specification
import com.genericgraph.api.domain.*
import org.neo4j.graphdb.*;

class QueryServiceSpec extends Specification {
    QueryService queryService;
    NodeService nodeService;

    def void setup() {
        nodeService = Mock()
        queryService = new QueryService(nodeService)
    }

    def "Can find by a node parameter" () {
        given:
        GenericNode resultNode = new GenericNode()
        resultNode.label = ['Person']
        resultNode.values = ['name':'itsMe']
        GenericQuery query = new GenericQuery(nodes: [new QueryParameter('Label', '=', 'Person')])
        GroovySpy(GenericNode, global:true)

        when:
        GenericNode personNode = queryService.find(query)

        then:
        1 * nodeService.find(_) >> null
        new GenericNode(_) >> resultNode
        personNode.values['name'] == 'itsMe'
    }
}