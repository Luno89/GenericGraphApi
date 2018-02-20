package com.genericgraph.api;

import java.util.ArrayList;
import org.neo4j.graphdb.*;
import java.util.HashMap;
import java.util.List;

import com.genericgraph.api.domain.*;

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
        String queryStart = "MATCH (n) WHERE ";
        String queryEnd = "RETURN n";

        String values = getValuesString(query.nodes);
        String labels = getLabelString(query.labels);
        String where = values != "" && labels != "" ? values + "AND " + labels : values + labels;

        String queryString = queryStart + where + queryEnd;

        return (Node)db.execute(queryString).next().get("n");
    }

    public GenericNode findGeneric(GenericQuery query) {
        return new GenericNode(this.find(query));
    }

    // TO-DO DRY this up
    public ArrayList<Node> findAll(GenericQuery query) {
        String queryStart = "MATCH (n) WHERE ";
        String queryEnd = "RETURN n";

        String values = getValuesString(query.nodes);
        String labels = getLabelString(query.labels);
        String where = values != "" && labels != "" ? values + "AND " + labels : values + labels;

        String queryString = queryStart + where + queryEnd;

        ArrayList<Node> results = new ArrayList<>();
        db.execute(queryString).forEachRemaining(it -> {
            results.add((Node)it.get("n"));
        });
        return results;
    }

    private String getValuesString(ArrayList<QueryParameter> parameters) {
        StringBuilder valuesString = new StringBuilder();
        String query = "";

        if (parameters != null) {
            parameters.forEach((QueryParameter p) -> {
                valuesString.append(p.toString() + " AND ");
            });
        }

        if (valuesString.length() > 4) {
            query = valuesString.toString().substring(0, valuesString.length()-4);
        }
        return query;
    }

    private String getLabelString(ArrayList<String> labelList) {
        if (labelList != null && labelList.size() > 0) {
            StringBuilder labels = new StringBuilder();
            labelList.forEach(s -> {
                labels.append(":" + s);
            });
            return "n" + labels.toString() + " ";
        }
        return "";
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

        String whereString = "WHERE " + getValuesString(query.relationships);
        String labels = getLabelString(query.labels);
        Result result = db.execute("MATCH ()-["+ labels + "]-() " + whereString + " RETURN n");
        
        ResourceIterator<org.neo4j.graphdb.Relationship> relationships = result.columnAs("n");
        relationships.forEachRemaining(it -> {
            relationshipResults.add(it);
        });

        return relationshipResults;
    }
}