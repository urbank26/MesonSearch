import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import org.jlab.groot.data.H1F;
import org.jlab.groot.math.F1D;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.hipo4.data.*;

import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.EventFilter;
import org.jlab.physics.io.DataManager;
public class Calculate {
//---- getPhysicsEvents METHOD----//

    public ArrayList<PhysicsEvent> getPhysicsEvents(HipoReader reader, Event event, Bank particles) {
        //public ArrayList<PhysicsEvent> getPhysicsEvents() {

        reader.getEvent(event, 0);

        ArrayList<PhysicsEvent> validList = new ArrayList<>();

        reader.getEvent(event, 0);
        EventFilter eventFilter = new EventFilter("211:-211:22:22:X+:X-:Xn");

        while (reader.hasNext()) {
            reader.nextEvent(event);
            event.read(particles);
            PhysicsEvent physEvent = DataManager.getPhysicsEvent(6, particles);
            if (eventFilter.isValid(physEvent)) {
                validList.add(physEvent);
            }
        }
        System.out.println(validList.size());
        System.out.println("test one, PhyEvent class done");
        return validList;
    }

//---- getTwoPhotonMass METHOD ----//

    public ArrayList<ArrayList<?>> getTwoPhotonMass(Graphing graphing, Calculate calculate, ArrayList<PhysicsEvent> physEvents, LorentzVector vL_pi0) {

        int physEventCount = physEvents.size();
        ArrayList<Double> invMass = new ArrayList<>();
        ArrayList<PhysicsEvent> qualEvents = new ArrayList<>();
        ArrayList<ArrayList<?>> comboList = new ArrayList<>();
        ArrayList<H1F> histoList = new ArrayList<>();
        ArrayList<Double> particleType1 = new ArrayList<>();
        ArrayList<Double> particleType2 = new ArrayList<>();
        H1F angleGraph = new H1F("Angle Graph", 100, 0, 50);
        F1D angleFunctionFit;
        int particleCount = 0;

        System.out.println("0: start");
        for (int eventNum = 0; eventNum < physEventCount; eventNum++) {
            PhysicsEvent physEvent = physEvents.get(eventNum);
            int gammaCount = physEvent.countByPid(22);
            //System.out.println("1: start test");
            for (int row = 0; row < gammaCount; row++) {
                boolean firstCheck = calculate.isTrue(physEvent, row, angleGraph);
                //System.out.println("2: come on");
                if (firstCheck){
                    //System.out.println("3: plese do");
                    for (int jow = row + 1; jow < gammaCount; jow++) {
                        boolean secondCheck = calculate.isTrue(physEvent, jow, null);
                        //System.out.println("4: getting there");
                        if (secondCheck){
                            //System.out.println("5: we are close");
                            String rownum = Integer.toString(row);
                            String jownum = Integer.toString(jow);
                            Particle gamma0 = physEvent.getParticleByPid(22, row);
                            Particle gamma1 = physEvent.getParticleByPid(22, jow);
                            vL_pi0.copy(gamma0.vector());
                            vL_pi0.add(gamma1.vector());
                            String parts = ("[22," + row + "]+[22," + jow + "]");
                            Particle pi0 = physEvent.getParticle(parts);
                            particleCount++;
                            particleType1.add(pi0.mass());
                            particleType2.add(vL_pi0.mass());
                            double pi0Mass = pi0.mass();
                            //System.out.println(pi0Mass);
                            if ((pi0Mass < 0.1563) && (0.1325 < pi0Mass)) {
                                //System.out.println("5: this is it");
                                //System.out.println(pi0Mass);
                                invMass.add(pi0Mass);
                                qualEvents.add(physEvent);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("# of masses: " + invMass.size());
        System.out.println("# of events: " + qualEvents.size());
        angleGraph.setTitleX("theta(#e vs #gamma");
        angleGraph.setTitleY("count");
        angleGraph.setFillColor(32);
        H1F pi0 = graphing.histro(particleType1, "Pi0 Histogram", "Type1: M(#gamma#gamma) [GeV]", "Counts", 100, .00, .28);
        H1F pi0vec = graphing.histro(particleType2, "Pi0Vec Histogram", "Type2: M(#gamma#gamma) [GeV]", "Counts", 100, .00, .28);
        histoList.add(pi0);
        histoList.add(pi0vec);
        histoList.add(angleGraph);
        comboList.add(invMass);
        comboList.add(qualEvents);
        comboList.add(histoList);
        System.out.println("6: gammaMass class done");
        return comboList;
    }

    public boolean isTrue(PhysicsEvent physEvent, int row, H1F graph) {

        int physEventCount = physEvent.countByPid(22);
        boolean pass = false;
        LorentzVector vL_electron = new LorentzVector();
        LorentzVector vL_gamma = new LorentzVector();
        Particle electron = physEvent.getParticleByPid(11, 0);
        Particle gamma = physEvent.getParticleByPid(22, row);
        vL_electron.copy(electron.vector());
        vL_gamma.copy(gamma.vector());
        double ex = vL_electron.vect().x();
        double ey = vL_electron.vect().y();
        double ez = vL_electron.vect().z();
        double gx = vL_gamma.vect().x();
        double gy = vL_gamma.vect().y();
        double gz = vL_gamma.vect().z();
        double emag = Math.sqrt(Math.pow(ex, 2) + Math.pow(ey, 2) + Math.pow(ez, 2));
        double gmag = Math.sqrt(Math.pow(gx, 2) + Math.pow(gy, 2) + Math.pow(gz, 2));
        double numerator = ((ex * gx) + (ey * gy) + (ez * gz));
        double denominator = (emag * gmag);
        double fraction = (numerator / denominator);
        double angle = Math.acos(fraction);
        //System.out.println("Electron Mass " + vL_electron.mass());
        //System.out.println("Elec, x: " + ex + "y: " + ey + "z: " + ez);
        //System.out.println("Gamm, x: " + gx + "y: " + gy + "z: " + gz);
        //System.out.println("Elec Mag: " + emag + "Gamm Mag: " + gmag);
        double thetaD = vL_electron.vect().theta(vL_gamma.vect());
        System.out.println("Calculated angle: " + angle);
        //System.out.println("Premade angle: " + thetaD);
        //hTheta.fill(Math.toDegrees(angle));
        //hTheta.fill(Math.toDegrees(thetaD));
        if (Math.toDegrees(angle) > 7) {;
            pass = true;
        }
        if (graph != null){
        graph.fill(Math.toDegrees(angle));
        }
    return pass;
    }

    public ArrayList<ArrayList<?>> mesonMass(Graphing graphing,ArrayList<PhysicsEvent> eventlist, ArrayList<Double> invarMass){

        int filteredEventsCount = 0;
        int qualifiedEventsCount = 0;

        ArrayList<ArrayList<?>> comboList = new ArrayList<>();
        ArrayList<PhysicsEvent> qualEvents = new ArrayList<>();
        ArrayList<Double> mesonInvMass = new ArrayList<>();
        ArrayList<H1F> graphs = new ArrayList<>();

        for(int row = 0; row < invarMass.size();row++){
            LorentzVector posPionVec = new LorentzVector();
            LorentzVector negPionVec = new LorentzVector();
            LorentzVector pionPairVec = new LorentzVector();

            filteredEventsCount++;
            Particle negPion = eventlist.get(row).getParticleByPid(211, 0);
            Particle posPion = eventlist.get(row).getParticleByPid(-211, 0);
            posPionVec.copy(posPion.vector());
            negPionVec.copy(negPion.vector());
            pionPairVec.add(negPionVec);
            pionPairVec.add(posPionVec);
            if(!qualEvents.contains(eventlist.get(row))){
                qualEvents.add(eventlist.get(row));
                double totalMesonMass =  pionPairVec.mass() + invarMass.get(row);
                mesonInvMass.add(totalMesonMass);
                qualifiedEventsCount++;
            }


        }
        System.out.println("calculate class: mass - " + mesonInvMass.get(1));
        H1F mesonMass = graphing.histro(mesonInvMass, "Meson Histogram", "M(#gamma#gamma#pi+#pi-) [GeV]", "Counts", 100, 0.0, 3.0);
        graphs.add(mesonMass);
        System.out.println("Meson Calculation: ");
        System.out.println("Filtered Events: " + filteredEventsCount + " || Qualified Events: " + qualifiedEventsCount);
        System.out.println("Events: " + qualEvents.size() + " || MassCount: " + mesonInvMass.size());
        comboList.add(qualEvents);
        comboList.add(mesonInvMass);
        comboList.add(graphs);
        return comboList;
       }
}

