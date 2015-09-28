package com.slightlyloony.sim.test;

import com.slightlyloony.sim.values.AssumedUnit;
import com.slightlyloony.sim.values.RequiredUnit;
import com.slightlyloony.sim.values.Units;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ScalarTest {

    @Test
    public void basic() {

        // factories...
        AssumedUnit au = new AssumedUnit( 0, Units.CAPACITANCE );
        RequiredUnit ru = new RequiredUnit( 0, Units.POWER );

        AssumedUnit auv = au.getInstance( "1.2pf", Units.CAPACITANCE );
        assertEquals( auv.getValue(), 1.2e-12, 1e-20 );
        assertEquals( auv.getUnit(), Units.CAPACITANCE );

        auv = au.getInstance( "1.2 u farad", Units.CAPACITANCE );
        assertEquals( auv.getValue(), 1.2e-6, 1e-20 );
        assertEquals( auv.getUnit(), Units.CAPACITANCE );

        auv = au.getInstance( "1.2 microfarads", Units.CAPACITANCE );
        assertEquals( auv.getValue(), 1.2e-6, 1e-20 );
        assertEquals( auv.getUnit(), Units.CAPACITANCE );

        auv = au.getInstance( "1.2µ", Units.CAPACITANCE );
        assertEquals( auv.getValue(), 1.2e-6, 1e-20 );
        assertEquals( auv.getUnit(), Units.CAPACITANCE );

        auv = au.getInstance( "1.2 kohms", Units.CAPACITANCE );
        assertNull( auv );

        RequiredUnit ruv = ru.getInstance( "5w", Units.POWER );
        assertEquals( ruv.getValue(), 5, 1e-20 );
        assertEquals( ruv.getUnit(), Units.POWER );

        ruv = ru.getInstance( "5 watts", Units.POWER );
        assertEquals( ruv.getValue(), 5, 1e-20 );
        assertEquals( ruv.getUnit(), Units.POWER );

        ruv = ru.getInstance( "5 kilowatts", Units.POWER );
        assertEquals( ruv.getValue(), 5000, 1e-20 );
        assertEquals( ruv.getUnit(), Units.POWER );

        ruv = ru.getInstance( "5", Units.POWER );
        assertNull( ruv );

        ruv = ru.getInstance( "5v", Units.POWER );
        assertNull( ruv );

        ruv = ru.getInstance( "5k", Units.POWER );
        assertNull( ruv );

        hashCode();
    }

}