package com.genericgraph.api;

import java.util.ArrayList;

import com.genericgraph.api.domain.GenericNode;
import com.genericgraph.api.domain.GenericQuery;
import com.genericgraph.api.domain.GenericRelationship;
import com.genericgraph.api.domain.QueryParameter;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

class NodeService {
    protected GraphDatabaseService db;

    NodeService(GraphDatabaseService injectedDb) {
        this.db = injectedDb;
    }

    public boolean write(GenericNode gNode) {
        Transaction tx = db.beginTx();
        Node node = db.createNode();

        addLabels(node, gNode);
        addValues(node, gNode);

        tx.success();
        return true;
    }

    private void addLabels(Node node, GenericNode gNode) {
        if (gNode.label != null) {
            gNode.label.forEach(it -> {
                node.addLabel(Label.label(it));
            });
        }
    }

    private void addValues(Node node, GenericNode gNode) {
        if (gNode.values != null) {
            gNode.values.forEach((k,v) ->{
                node.setProperty(k, v);
            });
        }
    }

    public boolean writeRelationship(GenericNode firstNode, GenericRelationship relationship, GenericNode secondNode) {
        Transaction tx = db.beginTx();

        Node firstFoundNode = find(firstNode);
        Node secondFoundNode = find(secondNode);

        org.neo4j.graphdb.Relationship graphRelationship = firstFoundNode.createRelationshipTo(secondFoundNode, RelationshipType.withName(relationship.name));

        if(relationship.values != null) {
            relationship.values.forEach( (String k, Object v) -> {
                graphRelationship.setProperty(k, v);
            });
        }

        tx.success();
        return true;
    }

    public Node find(GenericNode node) {
        GenericQuery query = new GenericQuery();

        query.labels = node.label;
        query.nodes = new ArrayList<>();
        node.values.forEach((k,v) -> {
            query.nodes.add(new QueryParameter(k,"=",v));
        });

        return find(query);
    }

    public Node find(GenericQuery query) {
        String queryString = new GenericQuery.Builder(query).build();

        return (Node)db.execute(queryString).next().get("n");
    }

    public GenericNode findGeneric(GenericQuery query) {
        return new GenericNode(this.find(query));
    }

    // TO-DO DRY this up
    public ArrayList<Node> findAll(GenericQuery query) {
        String queryString = new GenericQuery.Builder(query).build();

        ArrayList<Node> results = new ArrayList<>();
        db.execute(queryString).forEachRemaining(it -> {
            results.add((Node)it.get("n"));
        });
        return results;
    }

    public ArrayList<org.neo4j.graphdb.Relationship> findRelationships(String name) {
        ArrayList<org.neo4j.graphdb.Relationship> relationshipResults = new ArrayList<>();

        Result result = db.execute("MATCH ()-[n:"+ name + "]-() RETURN n");
        ResourceIterator<org.neo4j.graphdb.Relationship> relationships = result.columnAs("n");
        relationships.forEachRemaining(it -> {
            relationshipResults.add(it);
        });

        return relationshipResults;
    }

    public ArrayList<org.neo4j.graphdb.Relationship> findRelationships(GenericQuery query) {
        ArrayList<org.neo4j.graphdb.Relationship> relationshipResults = new ArrayList<>();

        String queryString = new GenericQuery.Builder(query).build();
        Result result = db.execute(queryString);
        
        ResourceIterator<org.neo4j.graphdb.Relationship> relationships = result.columnAs("n");
        relationships.forEachRemaining(it -> {
            relationshipResults.add(it);
        });

        return relationshipResults;
    }
}