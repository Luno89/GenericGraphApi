package com.genericgraph.api;

import spock.lang.Specification
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.test.rule.TestDirectory;
import org.junit.Rule;

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
        nodeService.writeRelationship(zach, new Relationship(name:'knows'), anna)

        then:
        Node zachsNode = db.execute('MATCH (p) WHERE p.name = "zach" RETURN p').next()['p']
        zachsNode.getRelationships().iterator().next().getStartNode().equals(zachsNode)
    }
}