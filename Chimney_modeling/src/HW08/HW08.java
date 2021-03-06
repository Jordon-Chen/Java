//607238002 2019_6_5
/////Given/////
/*1.the height of the chimney
  2.the discharging rate of the pollutants
  3.the wind speed and direction. In is version, the wind direction is N45E.
  4.the size of grid*/
/////Goal/////
/*1.calculate the concentration at each point in the 3-D area
  2.find the position where the maximum concentration exists*/
/////Constraints/////
/*1.the position of each point should be stored in three 3-D array
  2.scan the grids in the sequence (x-->y-->z)
  3.write a class that contains the position, height, discharge rate of the chimney
  4.write a class that contains the size, interval, wind direction, wind speed, xyz position and Concentration array of the base
  5.write a class that contains the method that calculate the concentration and the method that find the Maximum concentration
  6.read many chimneys from a input.txt
  7.output result to .txt
  8.change the base to a circular base. Extends the base class
  */

package HW08;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class HW08 {
    public static void main(String[] args) throws Exception {
        int numChi,i;
        //////////read file//////////
        ArrayList<Chimney> chimneyList = new ArrayList<>();
        numChi=readFile(chimneyList);
        //////////set base//////////
        CircularBase circularBase = new CircularBase(50000,50000,1000,1000,1000,25,45.0,3.0,20000.0);
        circularBase.initialize();//set the x, y and z in each point
        circularBase.initializeD();
        //////////calculate the pollution from each chimney//////////
        Chimney chi;
        Equation equation = new Equation();
        i=0;
        while(i < numChi){
            chi=chimneyList.get(i);
            System.out.println("chimney" +(i+1) +": " +"x=" +chi.getX() +" " +"y=" +chi.getY() +" " +"H=" +chi.getH() +" " +"Q=" +chi.getQ());
            equation.setBaseChimney(chi,circularBase);
            equation.G_model();
            equation.maxConcentration();//find the position where the maximum concentration exists
            circularBase.showCMax();
            System.out.println(" ");
            i++;
        }
        //////////output file//////////
        outputFile(circularBase);
    }
    public static int readFile(ArrayList chimneyList)throws Exception{
        int numChi;
        double x,y,H,Q;
        //String filename;
        //Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the filename of the chimney. EX: chimney.txt");
        //filename = scanner.next();
        //String path = "src/HW08/" + filename;
        File file = new File("src/HW08/chimney.txt");
        numChi=0;//number of the chimneys
        try (
                Scanner input = new Scanner(file);
        ){
            while(input.hasNext()){
                x=input.nextDouble();
                y=input.nextDouble();
                H=input.nextDouble();
                Q=input.nextDouble();
                numChi+=1;
                Chimney chimney = new Chimney(x,y,H,Q);
                chimneyList.add(chimney);
            }
        }
        return numChi;
    }
    public static void outputFile(CircularBase circularBase)throws Exception{
        int i,j,k;
        double C;
        String X,Y,Z;
        System.out.println(">>>>>Calculation completed. Check output.txt");
        //Scanner scanner = new Scanner(System.in);
        //String filename;
        //filename = scanner.next();
        //String pathOut = "src/HW08/" + filename;
        File fileOut = new File("src/HW08/output.txt");
        if (fileOut.exists()) {
            System.exit(0);}
        try (
                java.io.PrintWriter output = new java.io.PrintWriter(fileOut);
        ){
            for (k = 0; k < circularBase.getGridZ(); k++) {
                for (j = 0; j < circularBase.getGridY(); j++) {
                    for (i = 0; i < circularBase.getGridX(); i++) {
                        if(circularBase.getD(i,j,k)==1){
                            X=String.format("%1$-5s",Integer.toString(circularBase.getX(i,j,k)));
                            Y=String.format("%1$-5s",Integer.toString(circularBase.getY(i,j,k)));
                            Z=String.format("%1$-5s",Integer.toString(circularBase.getZ(i,j,k)));
                            C=circularBase.getC(i,j,k);
                            output.print(X +"\t" +Y +"\t" +Z +"\t" +C +"\r\n");
                        }
                    }
                }
            }
        }
    }
}
class Base {
    protected int lenghX,lenghY,lenghZ;
    protected int IntervalX, IntervalY,IntervalZ;
    protected int gridX, gridY,gridZ,XMax,YMax,ZMax;
    protected double u, windDirection,CMax;
    protected int[][][] X;
    protected int[][][] Y;
    protected int[][][] Z;
    protected double[][][] C;
    Base (){
        lenghX = 0;
        lenghY = 0;
        lenghZ = 0;
        IntervalX = 0;
        IntervalY = 0;
        IntervalZ = 0;
        windDirection = 0.0;
        u = 0.0;
        gridX = 1;
        gridY = 1;
        gridZ = 1;
        X = new int[gridZ][gridY][gridX];
        Y = new int[gridZ][gridY][gridX];
        Z = new int[gridZ][gridY][gridX];
        C = new double[gridZ][gridY][gridX];
        XMax = 0;
        YMax = 0;
        ZMax = 0;
        CMax = 0.0;
    }
    Base (int inputLenghX, int inputLenghY, int inputLenghZ, int inputIntervalX, int inputIntervalY, int inputIntervalZ, double inputWindDirection, double inputU){
        lenghX = inputLenghX;
        lenghY = inputLenghY;
        lenghZ = inputLenghZ;
        IntervalX = inputIntervalX;
        IntervalY = inputIntervalY;
        IntervalZ = inputIntervalZ;
        windDirection = inputWindDirection;
        u = inputU;
        gridX = (lenghX/inputIntervalX)+1;
        gridY = (lenghY/inputIntervalY)+1;
        gridZ = (lenghZ/inputIntervalZ)+1;
        X = new int[gridZ][gridY][gridX];
        Y = new int[gridZ][gridY][gridX];
        Z = new int[gridZ][gridY][gridX];
        C = new double[gridZ][gridY][gridX];
        XMax = 0;
        YMax = 0;
        ZMax = 0;
        CMax = 0.0;
    }
    public int getLengthX (){
        return lenghX;
    }
    public int getLengthY (){
        return lenghY;
    }
    public int getLengthZ (){
        return lenghZ;
    }
    public int getIntervalX (){
        return IntervalX;
    }
    public int getIntervalY (){
        return IntervalY;
    }
    public int getIntervalZ (){
        return IntervalZ;
    }
    public int getGridX (){
        return gridX;
    }
    public int getGridY (){
        return gridY;
    }
    public int getGridZ (){
        return gridZ;
    }
    public double getWindDirection (){
        return windDirection;
    }
    public double getU (){
        return u;
    }
    public int getX (int gridX, int gridY, int gridZ){
        return X[gridZ][gridY][gridX];
    }
    public int getY (int gridX, int gridY, int gridZ){
        return Y[gridZ][gridY][gridX];
    }
    public int getZ (int gridX, int gridY, int gridZ){
        return Z[gridZ][gridY][gridX];
    }
    public double getC (int gridX, int gridY, int gridZ){
        return C[gridZ][gridY][gridX];
    }
    public void setC(int gridX, int gridY, int gridZ, double inputConcentration){
        C[gridZ][gridY][gridX]+=inputConcentration;
    }
    public void setXMax(int inputXMax){
        XMax=inputXMax;
    }
    public void setYMax(int inputYMax){
        YMax=inputYMax;
    }
    public void setZMax(int inputZMax){
        ZMax=inputZMax;
    }
    public void setCMax(double inputCMax){
        CMax=inputCMax;
    }
    public void initialize(){
        int i,j,k,temp;
        //////////X-axis assignment//////////
        for(k=0;k<gridZ;k++){
            for(j=0;j<gridY;j++){
                temp=0;
                for(i=0;i<gridX;i++){//Z[k][j][0] is 0. We don't need to assign a value to it.
                    X[k][j][i]=temp;
                    temp+=IntervalX;
                }
            }
        }
        //////////Y-axis assignment//////////
        for(k=0;k<gridZ;k++){
            temp=0;
            for(j=1;j<gridY;j++){//Y[k][0][i] is 0. We don't need to assign a value to it.
                temp+=IntervalY;
                for(i=0;i<gridX;i++){
                    Y[k][j][i]=temp;
                }
            }
        }
        //////////Z-axis assignment//////////
        temp=0;
        for(k=1;k<gridZ;k++){//Z[0][j][i] is 0. We don't need to assign a value to it.
            temp+=IntervalZ;
            for(j=0;j<gridY;j++){
                for(i=0;i<gridX;i++){
                    Z[k][j][i]=temp;
                }
            }
        }
    }
    public void showPosition() {
        int i, j, k, x, y, z;
        for (k = 0; k < gridZ; k++) {
            for (j = 0; j < gridY; j++) {
                for (i = 0; i <gridX ; i++) {
                    x = X[k][j][i];
                    y = Y[k][j][i];
                    z = Z[k][j][i];
                    System.out.println("[" + i + "]"+"[" + j + "]" +"[" + k + "]=" +"(" + x + "," + y + "," + z + ")");
                }
            }
        }
    }
    public void showConcentration(){
        int i, j, k, x, y, z;
        double c;
        for (k = 0; k < gridZ; k++) {
            for (j = 0; j < gridY; j++) {
                for (i = 0; i < gridX ; i++) {
                    x = X[k][j][i];
                    y = Y[k][j][i];
                    z = Z[k][j][i];
                    c = C[k][j][i];
                    System.out.println("(" + x + "," + y + "," + z + ")="+c);
                }
            }
        }
    }
    public void showCMax(){
        System.out.println("Concentration MAX: " +CMax +" at " +"("+XMax +","+YMax +","+ZMax +")");
        //System.out.println("The maximum concentration of pollutants occurs at ("+XMax +","+YMax +","+ZMax +") where the concentration is "+CMax +"." );
    }
}
class CircularBase extends Base{
    private double diameter;
    private int[][][] D;//to decide whether a point should be calculated or not
    CircularBase() {
        super();
        lenghX = 0;
        lenghY = 0;
        lenghZ = 0;
        IntervalX = 0;
        IntervalY = 0;
        IntervalZ = 0;
        windDirection = 0.0;
        u = 0.0;
        gridX = 1;
        gridY = 1;
        gridZ = 1;
        X = new int[gridZ][gridY][gridX];
        Y = new int[gridZ][gridY][gridX];
        Z = new int[gridZ][gridY][gridX];
        C = new double[gridZ][gridY][gridX];
        XMax = 0;
        YMax = 0;
        ZMax = 0;
        CMax = 0.0;
        diameter = 0.0;
        D = new int[1][1][1];
    }
    CircularBase(int inputLenghX, int inputLenghY, int inputLenghZ, int inputIntervalX, int inputIntervalY, int inputIntervalZ, double inputWindDirection, double inputU, double inputDiameter){
        super();
        lenghX = inputLenghX;
        lenghY = inputLenghY;
        lenghZ = inputLenghZ;
        IntervalX = inputIntervalX;
        IntervalY = inputIntervalY;
        IntervalZ = inputIntervalZ;
        windDirection = inputWindDirection;
        u = inputU;
        gridX = (lenghX/inputIntervalX)+1;
        gridY = (lenghY/inputIntervalY)+1;
        gridZ = (lenghZ/inputIntervalZ)+1;
        X = new int[gridZ][gridY][gridX];
        Y = new int[gridZ][gridY][gridX];
        Z = new int[gridZ][gridY][gridX];
        C = new double[gridZ][gridY][gridX];
        XMax = 0;
        YMax = 0;
        ZMax = 0;
        CMax = 0.0;
        diameter = inputDiameter;
        D = new int[gridZ][gridY][gridX];
    }
    public void setDiameter(double inputDiameter){
        diameter=inputDiameter;
    }
    public double getDiameter(){
        return diameter;
    }
    public void setD(int gridX, int gridY, int gridZ, int type){
        D[gridZ][gridY][gridX] = type;//type = 0 or 1
    }
    public int getD(int gridX, int gridY, int gridZ){
        return D[gridZ][gridY][gridX];
    }
    public void showPosition() {
        int i, j, k, x, y, z;
        for (k = 0; k < gridZ; k++) {
            for (j = 0; j < gridY; j++) {
                for (i = 0; i <gridX ; i++) {
                    if(D[gridZ][gridY][gridX] == 1){
                        x = X[k][j][i];
                        y = Y[k][j][i];
                        z = Z[k][j][i];
                        System.out.println("[" + i + "]"+"[" + j + "]" +"[" + k + "]=" +"(" + x + "," + y + "," + z + ")");
                    }
                }
            }
        }
    }
    public void showConcentration(){
        int i, j, k, x, y, z;
        double c;
        for (k = 0; k < gridZ; k++) {
            for (j = 0; j < gridY; j++) {
                for (i = 0; i < gridX ; i++) {
                    if(D[k][j][i] == 1) {
                        x = X[k][j][i];
                        y = Y[k][j][i];
                        z = Z[k][j][i];
                        c = C[k][j][i];
                        System.out.println("(" + x + "," + y + "," + z + ")=" + c);
                    }
                }
            }
        }
    }
    public void initializeD(){
        int x0,y0,x,y,i,j,k;
        double d;
        x0=25000;
        y0=25000;
        for(k=0;k<gridZ;k++){
            for(j=0;j<gridY;j++){
                for(i=0;i<gridX;i++){
                    x = getX(i,j,k);
                    y = getY(i,j,k);
                    d = Math.pow((Math.pow(x-x0,2)+Math.pow(y-y0,2)),0.5);
                    if((d-diameter) <= 0){
                        D[k][j][i]=1;
                    }
                }
            }
        }
    }
}
class Chimney {
    private double X, Y, H, Q;
    Chimney (){
        X=0.0;
        Y=0.0;
        H=0.0;
        Q=0.0;
    }
    Chimney (double inputX, double inputY, double inputH, double inputQ){
        X=inputX;
        Y=inputY;
        H=inputH;
        Q=inputQ;
    }
    public void setX (double inputX){
        X=inputX;
    }
    public double getX (){
        return X;
    }
    public void setY (double inputY){
        Y=inputY;
    }
    public double getY (){
        return Y;
    }
    public void setH (double inputH){
        H=inputH;
    }
    public double getH (){
        return H;
    }
    public void setQ (double inputQ){
        Q=inputQ;
    }
    public double getQ (){
        return Q;
    }
}
class Equation {
    private CircularBase circularBase;
    private Chimney chimney;
    Equation(){
        circularBase = new CircularBase();
        chimney = new Chimney();
    }
    Equation(Chimney imputChimney, CircularBase inputBase){
        chimney=imputChimney;
        circularBase=inputBase;
    }
    public void setBaseChimney(Chimney imputChimney, CircularBase inputBase){
        chimney=imputChimney;
        circularBase=inputBase;
    }
    public void G_model(){
        double xNew, yNew, zNew, sigmaY, sigmaZ, Concentration, theta;
        int i, j, k=0;
        //////////calculate concentration//////////
        while(k<circularBase.getGridZ()){
            j=0;//j should be reset in each loop
            while(j<circularBase.getGridY()){
                i=0;//i should be reset in each loop
                while(i<circularBase.getGridX()){
                    if(circularBase.getD(i,j,k) == 1){//1:inside the circular base; 0:outside the circular base
                        //////////new_axis//////////
                        theta=Math.PI/180*circularBase.getWindDirection();//wind direction in this homework is set N45E
                        xNew=(-1)*Math.sin(theta)*(circularBase.getX(i,j,k)-chimney.getX())+(-1)*Math.cos(theta)*(circularBase.getY(i,j,k)-chimney.getY());
                        yNew=Math.cos(theta)*(circularBase.getX(i,j,k)-chimney.getX())+(-1)*Math.sin(theta)*(circularBase.getY(i,j,k)-chimney.getY());
                        zNew=circularBase.getZ(i,j,k);
                        //////////The downwind points should be calculated.//////////
                        if(xNew > 0){
                            sigmaY=0.22*xNew*Math.pow((1+0.0001*xNew), -0.5);//sigmaY:the deviation of the pollutants in the y-axis
                            sigmaZ=0.001*xNew;//sigmaZ the deviation of the pollutants in the z-axis
                            Concentration=(chimney.getQ()/(2*Math.PI*circularBase.getU()*sigmaY*sigmaZ))*
                                    Math.exp(-Math.pow(yNew, 2)/(2*Math.pow(sigmaY, 2)))*
                                    (Math.exp(-Math.pow((zNew-chimney.getH()), 2)/(2*Math.pow(sigmaZ, 2)))
                                            +Math.exp(-Math.pow((zNew+chimney.getH()), 2)/(2*Math.pow(sigmaZ, 2))));
                            circularBase.setC(i,j,k,Concentration);
                            //System.out.println("("+X[k][j][i] +","+Y[k][j][i] +","+Z[k][j][i] +")=>" +"("+xNew +","+yNew +","+zNew +")=" +Concentration);
                        }
                    }
                    i++;
                }
                j++;
            }
            k++;
        }
    }
    public void maxConcentration(){
        int i,j,k;
        double temp,difference;
        //////////find maximum concentration//////////
        temp=circularBase.getC(0,0,0);
        for(k=0;k < circularBase.getGridZ();k++){
            for(j=0;j < circularBase.getGridY();j++){
                for(i=0;i < circularBase.getGridX();i++){
                    difference=circularBase.getC(i,j,k)-temp;
                    if(difference>0){
                        temp=circularBase.getC(i,j,k);//update the current CMAX in the for loop
                        circularBase.setCMax(circularBase.getC(i,j,k));
                        circularBase.setXMax(i*circularBase.getIntervalX());
                        circularBase.setYMax(j*circularBase.getIntervalY());
                        circularBase.setZMax(k*circularBase.getIntervalZ());
                    }
                }
            }
        }
    }
}