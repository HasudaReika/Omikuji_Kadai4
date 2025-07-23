package omikuji4;

import java.util.ResourceBundle;

public interface Fortune {

	String disp();

	String DISP_STR = ResourceBundle.getBundle("omikuji4.fortune").getString("DISP_STR");

}
