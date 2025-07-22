package omikuji4;

import java.util.ResourceBundle;

public interface Fortune {

	String disp();

	String DISP_STR = ResourceBundle.getBundle("fortune").getString("DISP_STR");

}
