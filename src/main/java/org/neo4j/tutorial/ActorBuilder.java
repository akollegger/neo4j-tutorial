package org.neo4j.tutorial;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.UniqueFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.neo4j.tutorial.DatabaseHelper.ensureRelationshipInDb;

public class ActorBuilder
{
    private String actorName;
    private List<String> characterNames = new ArrayList<String>();
    private int cash = -1;
    private String wikipediaUri;

    public static ActorBuilder actor(String actorName)
    {
        return new ActorBuilder(actorName);
    }

    public ActorBuilder(String actorName)
    {
        this.actorName = actorName;
    }

    public ActorBuilder played(String... characterNames)
    {
        Collections.addAll(this.characterNames, characterNames);
        return this;
    }

    public ActorBuilder salary(int cash)
    {
        this.cash = cash;
        return this;
    }

    public void fact(GraphDatabaseService db)
    {
        Node actor = ensureActorIsInDb(db);
        ensureCharacterIsInDb(actor, db);
    }

    private Node ensureActorIsInDb(GraphDatabaseService db)
    {
        Node actor = getOrCreateUniqueActor(actorName, db);

        if(wikipediaUri != null) {
            actor.setProperty("wikipedia", wikipediaUri);
        }

        if (cash > 0)
        {
            actor.setProperty("salary", cash);
        }

        return actor;
    }

    private Node getOrCreateUniqueActor(String actorName, GraphDatabaseService graphDb)
    {
        UniqueFactory<Node> factory = new UniqueFactory.UniqueNodeFactory( graphDb, "actors" )
        {
            @Override
            protected void initialize( Node created, Map <String, Object> properties )
            {
                created.setProperty( "actor", properties.get( "actor" ) );
            }
        };
        return factory.getOrCreate( "actor", actorName );
    }

    private void ensureCharacterIsInDb(Node actor, GraphDatabaseService db)
    {
        for(String characterName : characterNames)
        {
            new CharacterBuilder(characterName).fact(db);

            Node character = db.index().forNodes("characters").get("character", characterName).getSingle();

            if (actor != null && character != null)
            {
                ensureRelationshipInDb(actor, DoctorWhoRelationships.PLAYED, character);
            }
        }
    }

    public ActorBuilder wikipedia(String wikipediaUri)
    {
        this.wikipediaUri = wikipediaUri;
        return this;
    }
}
