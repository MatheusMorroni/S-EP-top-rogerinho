
/*************************************************************************
 *  Fernando Tocantins, Raul Rocha, Victor Martins
 *************************************************************************/

public class DoubleGenerator {
    
    /**
     * @param left:    numero de particulas do lado esquerdo (L)
     * @param right:   numero de particulas do lado direito (R)
     * @param mRatio:       valor da massa das particulas do embulo sobre a massa das outras ap
     * @param middleDiameter: diametro do embulo
     */
    public static void Generator (int left, int right, double middleDiameter, double mRatio) {
        
        double pressureL = 1;    // pressão do lado esquerdo do embulo
        double pressureR = 0;    // pressão do lado direito do embulo
        double quociente = 0.0;    // relação entre left e right
        double rx = 0;            // posição em x
        double ry = 0;            // posição em y
        double vx = 0;            // velocidade em x
        double vy = 0;            // velocidade em y
        double radius = 0.002;    // raio das particulas (diferente do raio das particulas do embulo)
        double m = 1000;          // massa da particula do meio (embulo)
        double mass = m / mRatio; // massa das particulas (diferente da massa das particulas do embulo)
        int r = 0;                // red contribution to color
        int g = 0;                // green contribution to color
        int b = 0;                // blue contribution to color
        int maior = Math.max (left, right);    // igual ao numero de particulas do compartimento com mais particulas
        double[][] vL = new double[2][left + 1];// armazena as componentes da velocidade das particulas
        double vmedL = 0.0;        // valor medio da velocidade das particulas de um compartimento
        double[][] vR = new double[2][right + 1];
        double vmedR = 0.0;
        
        while (Math.abs ((pressureR / pressureL) - 1) > 0.00001) {
            
            // cria a informação quanto a velocidade das particulas a esquerda
            for (int i = 1; i <= left; i++) {
                vL[0][i] = StdRandom.uniform (-0.02, 0.02);
                vL[1][i] = StdRandom.uniform (-0.02, 0.02);
                vmedL += vL[0][i] * vL[0][i] + vL[1][i] * vL[1][i];
            }
            
            pressureL = left * mass * (vmedL / left);
            
            // cria a informação quanto a velocidade das particulas a direita
            quociente = Math.sqrt (((double) left) / right);
            //StdOut.println ("quociente " + quociente);
            for (int i = 1; i <= right; i++) {
                vR[0][i] = quociente * StdRandom.uniform (-0.02, 0.02);
                vR[1][i] = quociente * StdRandom.uniform (-0.02, 0.02);
                vmedR += vR[0][i] * vR[0][i] + vR[1][i] * vR[1][i];
            }
            
            pressureR = right * mass * (vmedR / right);
        }
        
        //StdOut.println(pressureR/pressureL);					//VOCF
        
        // printa a particula que representa o embulo
        StdOut.println (0.5 + " " + 0.5 + " " +
                StdRandom.uniform (-0.02, 0.02) / 1000000000 + " " +
                StdRandom.uniform (-0.02, 0.02) / 1000000000 + " " +
                ((middleDiameter / 2)) + " " + m + " " +
                0 + " " + 0 + " " + 0 + " " + 0);
        
        // cria a informação restante sobre as particulas da esquerda
        for (int i = 1; i <= left; i++) {
            rx = StdRandom.uniform (radius, 0.5 - middleDiameter);
            ry = StdRandom.uniform ();
            StdOut.println (rx + " " + ry + " " + vL[0][i] + " " +
                    vL[1][i] + " " + radius + " " +
                    mass + " " + 255 + " " + g + " " + b + " " + (-i));
        }
        
        // cria a informação restante sobre as particulas da direita
        for (int i = 1; i <= right; i++) {
            rx = StdRandom.uniform (0.5 + middleDiameter, 1 - radius);
            ry = StdRandom.uniform ();
            StdOut.println (rx + " " + ry + " " + vR[0][i] + " " +
                    vR[1][i] + " " + radius + " " +
                    mass + " " + r + " " + g + " " + 255 + " " + (i));
        }
    }
    
    public static void main (String[] args) {
        int left = Integer.parseInt (args[0]);        // numero de particulas do lado esquerdo (L)
        int right = Integer.parseInt (args[1]);        // numero de particulas do lado direito (R)
        double middleDiameter = Double.parseDouble (args[2]);        // diametro do embulo
        double mRatio = Double.parseDouble (args[3]);    // valor da massa das particulas do embulo
        
        // retorna quantas particulas serão criadas pelo CollisionSystem.java
        StdOut.println (left + " " + right);
        
        
        // gera a informação quanto todas as particulas que devem ser criadas pelo CollisionSystem.java
        Generator (left, right, middleDiameter, mRatio);
    }
}
