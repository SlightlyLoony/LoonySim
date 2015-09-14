package com.slightlyloony.sim.specs;

import com.google.gson.*;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CircuitSpec {

    private static Gson gson = getGson();

    private String name;
    private CircuitMetaSpec meta;
    private ComponentSpec components[];
    private NetSpec nets[];
    private MonitorSpec monitors[];


    public static CircuitSpec constructFromStream( final Reader _spec ) {
        return gson.fromJson( _spec, CircuitSpec.class );
    }


    public static CircuitSpec constructFromString( final String _spec ) {
        return gson.fromJson( _spec, CircuitSpec.class );
    }


    private static Gson getGson() {
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter( ComponentSpec.class, new ComponentSpecDeserializer( ) );
        return gb.create();
    }

    private static class ComponentSpecDeserializer implements JsonDeserializer<ComponentSpec> {


        @Override
        public ComponentSpec deserialize( final JsonElement json, final Type typeOfT, final JsonDeserializationContext context )
                throws JsonParseException {
            JsonObject jo = json.getAsJsonObject();
            String type = jo.get( "type" ).getAsString();
            if( (type != null) && !type.isEmpty() && SpecClassResolver.has( type ) ) {
                return gson.fromJson( json.toString(), SpecClassResolver.resolve( type ) );
            }
            throw new JsonParseException( "Unrecognizable ComponentSpec JSON: " + json.toString() );
        }
    }


    public CircuitMetaSpec getMeta() {
        return meta;
    }


    public ComponentSpec[] getComponents() {
        return components;
    }


    public MonitorSpec[] getMonitors() {
        return monitors;
    }


    public NetSpec[] getNets() {
        return nets;
    }


    public String getName() {
        return name;
    }
}
