package frogger;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Celal on 31-1-2016.
 */
public class FroggerApp extends Application {

    private AnimationTimer timer;

    private Pane root;

    private List<Node> cars = new ArrayList<Node>();
    private Node frog;

    private static int lives = 3;
    private boolean lose;
    private boolean win;
    private static HBox frogLives;
    private static double difficulty = 0.075;
    private static int level = 1;

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(800, 600);

        frog = initFrog();

        //text for level
        Text levelIndicator = new Text(Integer.toString(level));
        levelIndicator.setBoundsType(TextBoundsType.VISUAL);
        levelIndicator.setFill(Color.SNOW);

        Circle circle = new Circle(19, Color.BLUE);

        StackPane layout = new StackPane();
        layout.getChildren().addAll(circle, levelIndicator);
        layout.setTranslateX(800 - 19 * 2);

        root.getChildren().addAll(frog, initLives(), layout);


        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };

        timer.start();

        return root;
    }

    private Node initLives() {
        Circle liveOne = new Circle(19, Color.RED);
        Circle liveTwo = new Circle(19, Color.RED);
        Circle liveThree = new Circle(19, Color.RED);

        frogLives = new HBox(lives);
        frogLives.setTranslateX(0);
        frogLives.getChildren().addAll(liveOne, liveTwo, liveThree);

        return frogLives;
    }

    private Node initFrog() {
        Rectangle rect = new Rectangle(38, 38, Color.GREEN);
        rect.setTranslateY(600 - 39);
        rect.setTranslateX(400 - 39);

        return rect;
    }

    private Node spawnLeftCar() {
        Rectangle rect = new Rectangle(40, 40, Color.RED);
        rect.setTranslateY((generateRandomEvenNumber()) * 40);

        root.getChildren().add(rect);
        return rect;
    }

    private Node spawnRightCar() {
        Rectangle rect = new Rectangle(40, 40, Color.RED);
        rect.setTranslateY((generateRandomOddNumber()) * 40);
        rect.setTranslateX(800 - 40);

        root.getChildren().add(rect);
        return rect;
    }

    private int generateRandomOddNumber() {
        Random random = new Random();
        int Low = 1;
        int High = 14;
        int number = random.nextInt(High - Low) + Low;
        while (((number % 2) == 0)) { //while even
            number = random.nextInt();
        }
        return number;
    }

    private int generateRandomEvenNumber() {
        Random random = new Random();
        int Low = 1;
        int High = 14;
        int number = random.nextInt(High - Low) + Low;
        while (!((number % 2) == 0)) { //while not even
            number = random.nextInt();
        }
        return number;
    }

    private void onUpdate() {
        for (int i = 0; i < cars.size(); i++) {
            if ((1 & i) == 0)
                cars.get(i).setTranslateX(cars.get(i).getTranslateX() + 20/*Math.random() * 10*/);
            else
                cars.get(i).setTranslateX(cars.get(i).getTranslateX() - 20/*Math.random() * 10*/);
        }

        if (Math.random() < difficulty) {
            cars.add(spawnLeftCar());
            cars.add(spawnRightCar());
        }


        checkstate();
    }

    private void checkstate() {
        if (lives == 0) {
            timer.stop();

            HBox hBox = new HBox();
            hBox.setTranslateX(350);
            hBox.setTranslateY(250);
            root.getChildren().add(hBox);

            HBox hBox2 = new HBox();
            hBox2.setTranslateX(200);
            hBox2.setTranslateY(300);
            root.getChildren().add(hBox2);

            animationGame("YOU LOSE", hBox);
            animationGame("PRESS ENTER TO RESTART", hBox2);
            root.getChildren().remove(frog);
            lose = true;
        }

        for (Node car : cars) {
            if (car.getBoundsInParent().intersects(frog.getBoundsInParent())) {
                frog.setTranslateX(400 - 39);
                frog.setTranslateY(600 - 39);
                frogLives.getChildren().remove(lives - 1);
                lives--;
                return;
            }
        }

        if (frog.getTranslateY() <= 0) {
            timer.stop();

            HBox hBox = new HBox();
            hBox.setTranslateX(350);
            hBox.setTranslateY(250);
            root.getChildren().add(hBox);

            HBox hBox2 = new HBox();
            hBox2.setTranslateX(300);
            hBox2.setTranslateY(300);
            root.getChildren().add(hBox2);

            animationGame("YOU WIN", hBox);
            animationGame("PRESS ENTER", hBox2);

            difficulty += 0.075;
            win = true;
        }
    }


    private void animationGame(String state, HBox hBox) {
        for (int i = 0; i < state.toCharArray().length; i++) {
            char letter = state.charAt(i);

            Text text = new Text(String.valueOf(letter));
            text.setFont(Font.font(48));
            text.setOpacity(0);

            hBox.getChildren().add(text);

            FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
            ft.setToValue(1);
            ft.setDelay(Duration.seconds(i * 0.05));
            ft.play();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));

        primaryStage.getScene().setOnKeyPressed(e -> {
            switch ((e.getCode())) {
                case W:
                case UP:
                    frog.setTranslateY(frog.getTranslateY() - 40);
                    break;
                case S:
                case DOWN:
                    frog.setTranslateY(frog.getTranslateY() + 40);
                    break;
                case A:
                case LEFT:
                    frog.setTranslateX(frog.getTranslateX() - 40);
                    break;
                case D:
                case RIGHT:
                    frog.setTranslateX(frog.getTranslateX() + 40);
                    break;
                case ENTER:
                    if (lose) {
                        lives += 3;
                        difficulty = 0.075;
                        level = 1;
                        restart(primaryStage, new FroggerApp());
                    } else if (win) {
                        lives = 3;
                        level += 1;
                        restart(primaryStage, new FroggerApp());
                    }
                    System.out.println(difficulty);
                    break;
                case ESCAPE:
                    primaryStage.close();
                    break;
                default:
                    break;
            }

        });

        primaryStage.show();

    }

    public static void restart(Stage window, Application app) {
        try {
            app.start(window);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
