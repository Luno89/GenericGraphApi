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

    def "neo4f sanity check" () {
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

    def "NodeService can write a label" () {
        when:
        nodeService.write(new GenericNode(label: ['person']))

        then:
        db.execute('MATCH (p:person) RETURN p').size() == 1
    }
}