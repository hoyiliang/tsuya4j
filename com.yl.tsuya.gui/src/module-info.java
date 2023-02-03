module com.yl.tsuya.gui {
	requires javafx.controls;
	requires javafx.fxml;
	requires logback.classic;
	requires org.slf4j;
	requires org.controlsfx.controls;
	requires javafx.media;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
}
