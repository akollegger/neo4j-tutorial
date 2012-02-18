package org.neo4j.tutorial;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;

import static junit.framework.Assert.assertEquals;

/**
 * In this Koan we focus on paths in Cypher.
 */
public class Koan08d
{
    private static EmbeddedDoctorWhoUniverse universe;

    @BeforeClass
    public static void createDatabase() throws Exception
    {
        universe = new EmbeddedDoctorWhoUniverse(new DoctorWhoUniverseGenerator());
    }

    @AfterClass
    public static void closeTheDatabase()
    {
        universe.stop();
    }

    @Test
    public void shouldFindHowManyRegenerationsBetweenTomBakerAndChristopherEccleston() throws Exception
    {
        ExecutionEngine engine = new ExecutionEngine(universe.getDatabase());
        String cql = null;

        // YOUR CODE GOES HERE
        cql = "start tom=node:actors(actor='Tom Baker'),chris=node:actors(actor='Christopher Eccleston') " +
                "match path=(tom)-[r:REGENERATED_TO*]->(chris) " +
                " return length(path) as regenerations";

        ExecutionResult result = engine.execute(cql);

        assertEquals(5, result.javaColumnAs("regenerations").next());
    }

    @Test
    public void shouldFindTheLongestContinuousStoryArcWithTheMaster() throws Exception
    {
        ExecutionEngine engine = new ExecutionEngine(universe.getDatabase());
        String cql = null;

        // YOUR CODE GOES HERE
        cql="start master=node:characters(character='Master') " +
                "match (master)-[:APPEARED_IN]->(first), arcs=(first)-[:NEXT*]->() " +
                "where all(ep in nodes(arcs) where master-[:APPEARED_IN]->ep) " +
                "return length(arcs) as noOfPathHops " +
                "order by noOfPathHops desc limit 1";

        ExecutionResult result = engine.execute(cql);

        // noOfPathHops is one less than the number of episodes in a story arc
        final int noOfStories = 5;
        assertEquals(noOfStories - 1, result.javaColumnAs("noOfPathHops").next());
    }
}
