package com.alyamovsky.snakegame;

import com.alyamovsky.snakegame.model.Snake;
import com.alyamovsky.snakegame.model.food.RegularFruit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

public class SnakeGame extends ApplicationAdapter {
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 20;
    private ShapeRenderer shapeRenderer;
    private Snake snake;
    private RegularFruit food;
    private int direction;
    private boolean gameOver;
    private int score;
    private int level;
    private float elapsedTime = 0f;
    private SpriteBatch batch;
    private BitmapFont font;
    float speed = 6; // cells per second

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        snake = new Snake(GRID_SIZE, GRID_SIZE / 2, GRID_SIZE / 2);
        spawnFood();
        direction = Snake.DIRECTION_RIGHT;
        gameOver = false;
        score = 0;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        level = 1;
    }

    private void spawnFood() {
        food = null;
        Random random = new Random();
        food = new RegularFruit(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
    }

    private void moveSnake() {


        try {
            snake.move(direction);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            gameOver = true;
        }
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || gameOver) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && direction != Snake.DIRECTION_DOWN) {
            direction = Snake.DIRECTION_UP;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S) && direction != Snake.DIRECTION_UP) {
            direction = Snake.DIRECTION_DOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A) && direction != Snake.DIRECTION_RIGHT) {
            direction = Snake.DIRECTION_LEFT;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D) && direction != Snake.DIRECTION_LEFT) {
            direction = Snake.DIRECTION_RIGHT;
        }

        float delta = Gdx.graphics.getDeltaTime();
        elapsedTime += delta;

        if (elapsedTime >= 1 / speed) {
            elapsedTime = 0;
            moveSnake();
            if (food.equals(snake.getHead())) {
                snake.eat(food);
                score += 1;
                spawnFood();
            }
        }


        // Clear the screen
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw a grid background
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);

        // Draw food
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(food.getX() * CELL_SIZE, food.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        shapeRenderer.setColor(Color.GREEN);
        for (int i = 0; i < snake.getSize(); i++) {
            Snake.Segment part = snake.getSegment(i);

            float x = part.getX() * CELL_SIZE;
            float y = part.getY() * CELL_SIZE;

            shapeRenderer.rect(x, y, CELL_SIZE, CELL_SIZE);
        }

        shapeRenderer.end();

        // Draw score, level, and snake size
        batch.begin();
        font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Level: " + level, 10, Gdx.graphics.getHeight() - 30);
        font.draw(batch, "Snake Size: " + snake.getSize(), 10, Gdx.graphics.getHeight() - 50);
        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
