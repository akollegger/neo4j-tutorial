package org.neo4j.tutorial;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import static org.junit.Assert.assertThat;
import static org.neo4j.tutorial.matchers.ContainsOnlySpecificActors.containsOnlyActors;
import static org.neo4j.tutorial.matchers.ContainsSpecificNumberOfNodes.containsNumberOfNodes;

/**
 * In this Koan we start using the new traversal framework to find interesting
 * information from the graph about the Doctor's past life.
 */
public class Koan07
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
    public void shouldDiscoverHowManyDoctorActorsHaveParticipatedInARegeneration() throws Exception
    {
        Node theDoctor = universe.theDoctor();
        TraversalDescription regeneratedActors = null;

        // YOUR CODE GOES HERE
        regeneratedActors = Traversal.description()
                .relationships(DoctorWhoRelationships.PLAYED, Direction.INCOMING)
                .depthFirst()
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .evaluator(new Evaluator() {
                    public Evaluation evaluate(Path path) {
                        if (path.endNode().hasRelationship(DoctorWhoRelationships.REGENERATED_TO)) {
                            return Evaluation.INCLUDE_AND_CONTINUE;
                        }
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    }
                });

        assertThat(regeneratedActors.traverse(theDoctor).nodes(), containsNumberOfNodes(11));
    }

    @Test
    public void shouldFindTheFirstDoctor()
    {
        Node theDoctor = universe.theDoctor();
        TraversalDescription firstDoctor = null;

        // YOUR CODE GOES HERE
        firstDoctor = Traversal.description()
                .relationships(DoctorWhoRelationships.PLAYED, Direction.INCOMING)
                .breadthFirst()
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .evaluator(new Evaluator() {
                    public Evaluation evaluate(Path path) {
                        if (path.endNode().hasRelationship(Direction.OUTGOING, DoctorWhoRelationships.REGENERATED_TO) &&
                                !path.endNode().hasRelationship(Direction.INCOMING, DoctorWhoRelationships.REGENERATED_TO))
                        {
                            return Evaluation.INCLUDE_AND_CONTINUE;
                        }
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    }
                });
                

        assertThat(firstDoctor.traverse(theDoctor).nodes(), containsOnlyActors("William Hartnell"));
    }
}
