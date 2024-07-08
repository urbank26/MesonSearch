import org.jlab.groot.data.*;
import org.jlab.groot.math.F1D;
import org.jlab.groot.ui.*;

import java.util.ArrayList;
import java.util.List;
import org.jlab.groot.math.Func1D;


public class Graphing {
 int nextID = 33;
    public H1F histro(ArrayList<Double> massList, String name, String xTitle, String yTitle, int tSize, double leftEnd, double rightEnd) {
        nextID++;
        H1F histogram = new H1F(name, tSize, rightEnd, leftEnd);
        histogram.setTitleX(xTitle);
        histogram.setTitleY(yTitle);
        int length = massList.size();
        for (int num = 0; num < length; num++) {
            double mass = massList.get(num);
            //System.out.println(mass);
            histogram.fill(mass);
        }
        histogram.setFillColor(nextID);
        return histogram;

    }

    public void makeCanvas(String name, int horizontal, int vertical, int divideDen, int divideNum, ArrayList<?> list) {
        List<H1F> histogram = new ArrayList<>();
        List<F1D> function = new ArrayList<>();
        TCanvas canvy = new TCanvas(name, horizontal, vertical);
        //TCanvas canvy2 = new TCanvas(name, horizontal, vertical);
        canvy.divide(divideDen, divideNum);
        //canvy2.divide(divideDen, divideNum);
        Object obj =  list.get(0);
        if (obj instanceof H1F) {
            for (Object obj1: list){
                H1F histo = (H1F) obj1;
                histogram.add(histo);
            }
            for (int num = 0; num < histogram.size(); num++) {
                H1F graph = histogram.get(num);
                canvy.cd(num).draw(graph);
            }
        } else if (obj instanceof F1D) {
            for (Object obj1: list){
                F1D func = (F1D) obj1;
                function.add(func);
            }
            for (int num = 0; num < function.size(); num++) {
                F1D graph = function.get(num);
                canvy.cd(num).draw(graph);
            }
        }
        {

        }
    }

    public static F1D makeGauss(String name, double xlo, double xhi, List<Double> parameters) {
        F1D f1 = new F1D(name, "[amp]*gaus(x,[mean],[sigma])", xlo, xhi);
        if (!parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                f1.setParameter(i, parameters.get(i));
            }
        }
        f1.setLineWidth(3);
        f1.setLineColor(5);
        f1.setOptStat(111110);

        return f1;
    }
    public static F1D makePolyGauss(String name, double xlo, double xhi, List<Double> parameters) {
        F1D f1 = new F1D(name, "[amp]*gaus(x,[mean],[sigma]) + [A]+([B]*x)", xlo, xhi);
        if (!parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                f1.setParameter(i, parameters.get(i));
            }
        }
        f1.setLineWidth(3);
        f1.setLineColor(6);
        f1.setOptStat(111110);

        return f1;
    }
    public static F1D makePoly(String name, double xlo, double xhi, List<Double> parameters) {
        F1D f1 = new F1D(name, "([A]+([B]*x)+([C]*x*x)+([D]*x*x*X))", xlo, xhi);
        if (!parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                f1.setParameter(i, parameters.get(i));
            }
        }
        f1.setLineWidth(3);
        f1.setLineColor(7);
        f1.setOptStat(111110);

        return f1;
    }

/*
    public static F1D makeFunction(String name, String function, double xlo, double xhi, List<Double> parameters) {
        String functionOut = "x";
        String amp = "[amp]";
        String mean = "[mean]";
        String sigma = "[sigma]";
        F1D f1;
        if (function == "gauass") {
            functionOut ="[amp]*gaus(x,[mean],[sigma])" ;

        } else if (function == "polyGauss") {
            functionOut = "([amp]*gaus(x,[mean],[sigma])+([A]+([B]*x)+([C]*x*x)+([D]*x*x*X))";

        } else if (function == "poly") {
            functionOut = "([A]+([B]*x)+([C}*x*x)+([D]*x*x*x))";
        }
        F1D f1 = new F1D(name, functionOut, xlo, xhi);
        if (!parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                f1.setParameter(i, parameters.get(i));
            }

        } else {
            f1.setParameter(0, 500.0);
            f1.setParameter(1, .15);
            f1.setParameter(2, .1);
            f1.setParameter(3, 100.0);
            f1.setParameter(4, 10);
            f1.setParameter(5, 0.5);
            f1.setParameter(6, 50);
        }
        return f1;

        */
    }



