module com.yl.tsuya.gui {
	requires javafx.controls;
	requires javafx.graphics;
	requires logback.classic;
	requires org.slf4j;
	
	opens application to javafx.graphics, javafx.fxml;
}
