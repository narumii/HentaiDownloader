package xyz.ethyr.conventer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.Scanner;

@AllArgsConstructor @Getter
public abstract class Downloader {

    private final File dir;
    private final Scanner scanner;

    public abstract void parserArguments();

    public abstract void downloadImages();
}
