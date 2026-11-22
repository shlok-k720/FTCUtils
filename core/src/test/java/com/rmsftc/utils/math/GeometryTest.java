package com.rmsftc.utils.math;

import com.pedropathing.drivetrain.DrivePowers;
import com.rmsftc.utils.AllianceColor;
import com.rmsftc.utils.DriveVector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryTest {

    private static final double EPS = 1e-9;

    @Test
    void poseIsAPedroPoseAndKeepsType() {
        Pose pose = new Pose(10.0, 20.0, Math.PI / 2.0);
        // Usable anywhere Pedro expects its own Pose.
        com.pedropathing.math.Pose pedroPose = pose;
        assertEquals(10.0, pedroPose.x(), EPS);

        Pose moved = pose.plus(new Pose(5.0, -5.0, 0.0));
        assertEquals(15.0, moved.x(), EPS);
        assertEquals(15.0, moved.y(), EPS);
        assertEquals(Math.PI / 2.0, moved.heading(), EPS);

        Pose roundTrip = Pose.fromPedro(pedroPose);
        assertEquals(pose.x(), roundTrip.x(), EPS);
        assertEquals(pose.heading(), roundTrip.heading(), EPS);
    }

    @Test
    void poseExtras() {
        Pose pose = new Pose(0.0, 0.0, Math.PI);
        assertEquals(180.0, pose.headingDegrees(), EPS);
        assertEquals(Math.PI / 2.0, pose.withHeadingDegrees(90.0).heading(), EPS);
        Pose target = new Pose(3.0, 4.0, 0.0);
        assertEquals(25.0, pose.distanceSquared(target), EPS);
        assertEquals(Math.atan2(4.0, 3.0), pose.headingTo(target), EPS);
    }

    @Test
    void poseIsMutable() {
        Pose pose = new Pose(1.0, 2.0, 0.0);
        pose.setX(10.0);
        pose.setY(20.0);
        pose.setHeading(Math.PI / 2.0);
        assertEquals(10.0, pose.x(), EPS);
        assertEquals(20.0, pose.y(), EPS);
        assertEquals(Math.PI / 2.0, pose.heading(), EPS);

        // Operations read the mutated values, not the constructor ones.
        Pose moved = pose.plus(new Pose(5.0, -5.0, 0.0));
        assertEquals(15.0, moved.x(), EPS);
        assertEquals(15.0, moved.y(), EPS);
        assertEquals(5.0, pose.distance(new Pose(13.0, 24.0, 0.0)), EPS);

        // Pedro-typed reads see the mutation too.
        com.pedropathing.math.Pose asPedro = pose;
        assertEquals(10.0, asPedro.x(), EPS);

        // withX still returns a new pose and leaves this one alone.
        Pose shifted = pose.withX(99.0);
        assertEquals(99.0, shifted.x(), EPS);
        assertEquals(10.0, pose.x(), EPS);

        // setHeading normalizes like the constructor.
        pose.setHeading(3.0 * Math.PI);
        assertEquals(Math.PI, pose.heading(), EPS);

        pose.set(7.0, 8.0, Math.PI / 4.0);
        assertEquals(7.0, pose.x(), EPS);
        assertEquals(8.0, pose.y(), EPS);
        assertEquals(Math.PI / 4.0, pose.heading(), EPS);
        assertEquals("(7.0, 8.0, " + (Math.PI / 4.0) + ")", pose.toString());
    }

    @Test
    void vectorIsMutable() {
        Vector v = new Vector(1.0, 2.0, 3.0);
        v.set(0, 10.0);
        assertEquals(10.0, v.get(0), EPS);
        assertEquals(Math.sqrt(113.0), v.magnitude(), EPS);

        v.setAll(4.0, 5.0, 6.0);
        assertEquals(6.0, v.get(2), EPS);
    }

    @Test
    void vector2dIsMutable() {
        Vector2D v = Vector2D.cartesian(3.0, 4.0);
        v.setX(6.0);
        assertEquals(6.0, v.x(), EPS);
        assertEquals(4.0, v.y(), EPS);
        v.setY(8.0);
        assertEquals(10.0, v.magnitude(), EPS);
        v.set(1.0, 1.0);
        assertEquals(1.0, v.x(), EPS);
        assertEquals(1.0, v.y(), EPS);
    }

    @Test
    void vectorKeepsType() {
        Vector v = new Vector(1.0, 2.0, 3.0);
        com.pedropathing.math.Vector pedroVector = v;
        assertEquals(3, pedroVector.size());

        Vector doubled = v.times(2.0);
        assertEquals(2.0, doubled.get(0), EPS);
        assertEquals(6.0, doubled.get(2), EPS);
        assertEquals(Math.sqrt(14.0), v.magnitude(), EPS);

        Vector sum = v.plus(new Vector(10.0, 20.0, 30.0));
        assertEquals(11.0, sum.get(0), EPS);
        assertEquals(33.0, sum.get(2), EPS);
    }

    @Test
    void vector2dWrapsPedro() {
        Vector2D v = Vector2D.cartesian(3.0, 4.0);
        assertEquals(5.0, v.magnitude(), EPS);
        assertEquals(3.0, v.toPedro().x(), EPS);

        Vector2D sum = v.plus(Vector2D.cartesian(1.0, 1.0));
        assertEquals(4.0, sum.x(), EPS);
        assertEquals(5.0, sum.y(), EPS);

        Pose pose = v.toPose(Math.PI / 2.0);
        assertEquals(3.0, pose.x(), EPS);
        assertEquals(Math.PI / 2.0, pose.heading(), EPS);

        assertEquals(Vector2D.cartesian(3.0, 4.0), v);
        assertEquals(Vector2D.cartesian(3.0, 4.0).hashCode(), v.hashCode());
    }

    @Test
    void driveVectorExtendsDrivePowers() {
        DriveVector drive = new DriveVector(1.0, 0.5, -0.25);
        DrivePowers powers = drive;
        assertEquals(1.0, powers.forward(), EPS);
        assertEquals("(1.00, 0.50, -0.25)", drive.toString());

        drive.forward(0.0);
        assertEquals(0.0, drive.forward(), EPS);

        DriveVector scaled = drive.copy();
        scaled.scale(2.0);
        assertEquals(1.0, scaled.strafe(), EPS);
        assertEquals(0.5, drive.strafe(), EPS); // copy is independent
    }

    @Test
    void driveVectorFieldCentric() {
        // Mirrors the 20373 DriveVectorTest: heading PI/2 turns (1, 0, 0)
        // into forward 0, strafe -1.
        DriveVector vector = new DriveVector(1.0, 0.0, 0.0);
        vector.fieldCentric(Math.PI / 2.0);
        assertEquals(0.0, vector.forward(), EPS);
        assertEquals(-1.0, vector.strafe(), EPS);

        DriveVector headingCtor = new DriveVector(0.0, 1.0, 0.0, Math.PI / 2.0);
        assertEquals(1.0, headingCtor.forward(), EPS);
        assertEquals(0.0, headingCtor.strafe(), EPS);
    }

    @Test
    void driveVectorFieldCentricWithAlliance() {
        // Blue's 180-degree offset flips the rotation.
        DriveVector blue = new DriveVector(1.0, 0.0, 0.0);
        blue.fieldCentric(0.0, AllianceColor.BLUE);
        assertEquals(-1.0, blue.forward(), EPS);
        assertEquals(0.0, blue.strafe(), EPS);

        DriveVector red = new DriveVector(1.0, 0.0, 0.0);
        red.fieldCentric(0.0, AllianceColor.RED);
        assertEquals(1.0, red.forward(), EPS);
        assertTrue(AllianceColor.from(true) == AllianceColor.RED);
    }
}
