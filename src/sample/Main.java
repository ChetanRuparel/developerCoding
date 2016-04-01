package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.awt.*;
import java.awt.Button;
import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{

        stage.setTitle("Movie Player");
        Group root = new Group();
        String path= "F:/MoviePlayer/videos/Burn.mp4";
        Media media = new Media(new File(path).toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        MediaView view  = new MediaView(player);
        Slider sliderV = new Slider();
        HBox buttons = new HBox();


        javafx.scene.control.Button playButton = new javafx.scene.control.Button("play");
        javafx.scene.control.Button pauseButton = new javafx.scene.control.Button("pause");
        javafx.scene.control.Button fullScreenButton = new javafx.scene.control.Button("full Screen");
        buttons.getChildren().add(playButton);
        buttons.getChildren().add(pauseButton);
        buttons.getChildren().add(fullScreenButton);

        final Timeline slideIn = new Timeline();
        final Timeline slideOut = new Timeline();


        root.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                slideOut.play();
            }
        });
        root.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                slideIn.play();
            }
        });
        final VBox vbox = new VBox();
        Slider slider = new Slider();
        vbox.getChildren().add(buttons);
        vbox.getChildren().add(sliderV);
        vbox.getChildren().add(slider);

        final HBox hbox = new HBox(2);
        final int bands = player.getAudioSpectrumNumBands();
        final Rectangle[] rects = new Rectangle[bands];
        for(int i=0 ;i<rects.length ;i++)
        {
            rects[i] = new Rectangle();
            rects[i].setFill(Color.GREENYELLOW);
            hbox.getChildren().add(rects[i]);

        }
        vbox.getChildren().add(hbox);
        root.getChildren().add(view);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root , 400,400, Color.BLACK);
        stage.setScene(scene);
        fullScreenButton.setOnAction((ActionEvent e) -> {
            if (stage.isFullScreen()) {
                stage.setFullScreen(false);
            } else {
                stage.setFullScreen(true);
            }
        });
        stage.show();

        player.play();
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                int w = player.getMedia().getWidth();
                int h = player.getMedia().getHeight();

                hbox.setMinWidth(w);
                int bandsWidth = w / rects.length;
                for (Rectangle r : rects) {
                    r.setWidth(bandsWidth);
                    r.setHeight(2);
                }

                stage.setMinHeight(h);
                stage.setMinWidth(w);

                vbox.setMinSize(w, 100);
                vbox.setTranslateY(h - 100);

                slider.setMin(0.0);
                slider.setValue(0.0);
                slider.setMax(player.getTotalDuration().toSeconds());
                sliderV.setMin(0.0);
                sliderV.setMax(100);

                slideOut.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(vbox.translateYProperty(), h - 100),
                                new KeyValue(vbox.opacityProperty(), 0.9)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vbox.translateYProperty(), h),
                                new KeyValue(vbox.opacityProperty(), 0.0)

                        )
                );
                slideIn.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(vbox.translateYProperty(), h),
                                new KeyValue(vbox.opacityProperty(), 0.0)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vbox.translateYProperty(), h - 100),
                                new KeyValue(vbox.opacityProperty(), 0.9)
                        )
                );
            }

        });
        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration current) {
                slider.setValue(current.toSeconds());
            }
        });
        slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                player.seek(Duration.seconds(slider.getValue()));
            }
        });
        player.setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double timestamp, double duration, float[] mags, float[] phases) {
                for (int i = 0; i < rects.length; i++) {
                    double h = mags[i] + 60;
                    if (h > 2) {
                        rects[i].setHeight(h);
                    }
                }
            }
        });


        sliderV.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        player.setVolume(sliderV.getValue() / 100);

                    }
                });
            }
        });
        pauseButton.setOnAction((ActionEvent e) -> {
            player.pause();
        });
        playButton.setOnAction((ActionEvent e) -> {
            player.play();
        });




    }



    public static void main(String[] args) {
        launch(args);
    }
}
