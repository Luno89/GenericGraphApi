package com.genericgraph.api;

import spock.lang.Specification
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.*;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.test.rule.TestDirectory;
import org.junit.Rule;
import com.genericgraph.api.domain.*;

class NodeServiceSpec extends Specification {
    @Rule
    TestDirectory testDirectory = TestDirectory.testDirectory()
    GraphDatabaseService db
    NodeService nodeService

    def void setup() {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase( testDirectory.directory())
        nodeService = new NodeService(db)
    }

    def cleanup() {
        db.shutdown()
    }

    def "neo4j sanity check" () {
        given:
        Node n = null        
        try {
            Transaction tx = db.beginTx()
            n = db.createNode()
            n.setProperty("name","beth")
            tx.success()
        } catch(Exception e) {
            throw e
        }

        when:
        Node foundNode = db.getNodeById(n.getId())

        then:
        foundNode.getId() == n.getId()
        foundNode.getProperty('name') == 'beth'
    }

    def "can write a label" () {
        when:
        nodeService.write(new GenericNode(label: ['person']))

        then:
        db.execute('MATCH (p:person) RETURN p').size() == 1
    }

    def "can write more than one label" () {
        when:
        nodeService.write(new GenericNode(label: ['person', 'furry']))

        then:
        db.execute('MATCH (p:person) RETURN labels(p) as p').next()['p'].size() == 2
    }

    def "can write values" () {
        when:
        nodeService.write(new GenericNode(values: ['firstname': 'zach', 'lastname':'zeman']))

        then:
        Node zach = db.execute('MATCH (p) WHERE p.firstname = "zach" RETURN p').next()['p']
        zach.getProperty('firstname') == 'zach'
        zach.getProperty('lastname') == 'zeman'
    }

    def "can write relationships" () {
        given:
        GenericNode zach = new GenericNode(label:['person'], values:['name':'zach'])
        GenericNode anna = new GenericNode(label:['person'], values:['name':'anna'])
        nodeService.write(zach)
        nodeService.write(anna)

        when:
        nodeService.writeRelationship(zach, new GenericRelationship(name:'knows'), anna)

        then:
        Node zachsNode = db.execute('MATCH (p) WHERE p.name = "zach" RETURN p').next()['p']
        zachsNode.getRelationships().iterator().next().getStartNode().equals(zachsNode)
    }

    def "can write relationship properties" () {
        given:
        GenericNode zach = new GenericNode(label:['person'], values:['name':'zach'])
        GenericNode anna = new GenericNode(label:['person'], values:['name':'anna'])
        GenericRelationship zachKnwsAnna = new GenericRelationship();
        zachKnwsAnna.name = 'knows'
        zachKnwsAnna.values = ['years':3, 'distance':7]
        nodeService.write(zach)
        nodeService.write(anna)

        when:
        nodeService.writeRelationship(zach, zachKnwsAnna, anna)

        then:
        Node zachsNode = db.execute('MATCH (p) WHERE p.name = "zach" RETURN p').next()['p']
        org.neo4j.graphdb.Relationship zachKnowingAnna = zachsNode.getRelationships().iterator().next()
        zachKnowingAnna.getStartNode().equals(zachsNode)
        zachKnowingAnna.getProperty('years') == 3
    }

    def "can find nodes on multiple values" () {
        given:
        GenericNode zachZeman = new GenericNode(label:['person'], values:['name':'zach','last':'zeman'])
        GenericNode zachFish = new GenericNode(label:['person'], values:['name':'zach','last':'fish'])
        nodeService.write(zachZeman)
        nodeService.write(zachFish)

        when:
        Node zachFishsNode = nodeService.find(zachFish)

        then:
        zachFishsNode.getProperty('name') == 'zach'
        zachFishsNode.getProperty('last') == 'fish'
    }

    def "can find nodes on multiple labels" () {
        given:
        GenericNode zachZeman = new GenericNode(label:['person', 'furry'], values:['name':'zach'])
        GenericNode zachFish = new GenericNode(label:['person', 'otaku'], values:['name':'zach'])
        nodeService.write(zachZeman)
        nodeService.write(zachFish)
        GenericQuery query = new GenericQuery(labels: ['person','otaku'],nodes: [new QueryParameter('name', '=', 'zach')])

        when:
        GenericNode zachFishsNode = nodeService.findGeneric(query)

        then:
        zachFishsNode.label.contains('otaku')
        zachFishsNode.values.get('name') == 'zach'
    }

    def "can find nodes on multiple labels without values" () {
        given:
        GenericNode zachZeman = new GenericNode(label:['person', 'furry'])
        GenericNode zachFish = new GenericNode(label:['person', 'otaku'])
        nodeService.write(zachZeman)
        nodeService.write(zachFish)

        when:
        GenericNode zachFishsNode = new GenericNode(nodeService.find(zachFish))

        then:
        zachFishsNode.label.contains('otaku')
    }

    def "can find nodes on relationship name" () {
        given:
        GenericNode zachZeman = new GenericNode(label:['person', 'furry'], values:['name':'zach'])
        GenericNode zachFish = new GenericNode(label:['person', 'otaku'], values:['name':'zach'])
        nodeService.write(zachZeman)
        nodeService.write(zachFish)

        nodeService.writeRelationship(zachZeman, new GenericRelationship(name:'knows',), zachFish)

        when:
        org.neo4j.graphdb.Relationship zachKnowingZachInWV = nodeService.findRelationships('knows')[0]

        then:
        zachKnowingZachInWV.getType().name() == 'knows'
    }

    def "can find nodes on relationship properties" () {
        given:
        GenericNode zachZeman = new GenericNode(label:['person', 'furry'], values:['name':'zach'])
        GenericNode zachFish = new GenericNode(label:['person', 'otaku'], values:['name':'zach'])
        GenericRelationship zzKnowsZfInMo = new GenericRelationship(name:'knows')
        zzKnowsZfInMo.values = ['years':7, 'where':'MO']
        GenericRelationship zzKnowsZfInWV = new GenericRelationship(name:'knows')
        zzKnowsZfInWV.values = ['years':10, 'where':'WV']
        nodeService.write(zachZeman)
        nodeService.write(zachFish)
        nodeService.writeRelationship(zachZeman, zzKnowsZfInMo, zachFish)
        nodeService.writeRelationship(zachZeman, zzKnowsZfInWV, zachFish)
        GenericQuery query = new GenericQuery(relationships: [new GenericQueryRelationship(label: 'knows', parameters: [new QueryParameter('years','>',9)])])

        when:
        org.neo4j.graphdb.Relationship zachKnowingZachInWV = nodeService.findRelationships(query)[0]

        then:
        zachKnowingZachInWV.getType().name() == 'knows'
        zachKnowingZachInWV.getProperty('where') == 'WV'
    }

    def "can find node on generic query" () {
        given:
        GenericNode zachZeman = new GenericNode(label:['person', 'furry'], values:['name':'zach', 'age':32])
        GenericNode zachFish = new GenericNode(label:['person', 'otaku'], values:['name':'zach', 'age':27])
        GenericRelationship zzKnowsZfInMo = new GenericRelationship(name:'knows')
        zzKnowsZfInMo.values = ['years':7, 'where':'MO']
        GenericRelationship zzKnowsZfInWV = new GenericRelationship(name:'knows')
        zzKnowsZfInWV.values = ['years':10, 'where':'WV']
        nodeService.write(zachZeman)
        nodeService.write(zachFish)
        nodeService.writeRelationship(zachZeman, zzKnowsZfInMo, zachFish)
        nodeService.writeRelationship(zachZeman, zzKnowsZfInWV, zachFish)

        when:
        org.neo4j.graphdb.Node zachZemansNode = nodeService.find(new GenericQuery(nodes:[new QueryParameter('age', '>', 30)]))

        then:
        zachZemansNode.getProperty('age') == 32
    }

    def "can find nodes on generic query" () {
        given:
        GenericNode zachZeman = new GenericNode(label:['person', 'furry'], values:['name':'zach', 'age':32])
        GenericNode zachFish = new GenericNode(label:['person', 'otaku'], values:['name':'zach', 'age':27])
        GenericRelationship zzKnowsZfInMo = new GenericRelationship(name:'knows')
        zzKnowsZfInMo.values = ['years':7, 'where':'MO']
        GenericRelationship zzKnowsZfInWV = new GenericRelationship(name:'knows')
        zzKnowsZfInWV.values = ['years':10, 'where':'WV']
        nodeService.write(zachZeman)
        nodeService.write(zachFish)
        nodeService.writeRelationship(zachZeman, zzKnowsZfInMo, zachFish)
        nodeService.writeRelationship(zachZeman, zzKnowsZfInWV, zachFish)

        when:
        ArrayList<org.neo4j.graphdb.Node> nodes = nodeService.findAll(new GenericQuery(nodes:[new QueryParameter('name', '=', 'zach')]))

        then:
        nodes.size() == 2
    }
}