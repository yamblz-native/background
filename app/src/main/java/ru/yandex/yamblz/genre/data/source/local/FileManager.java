package ru.yandex.yamblz.genre.data.source.local;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by platon on 10.08.2016.
 */
public class FileManager
{
    public void writeToFile(File file, String fileContent)
    {
        if (!file.exists())
        {
            try
            {
                FileWriter writer = new FileWriter(file);
                writer.write(fileContent);
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public String readFileContent(File file)
    {
        StringBuilder fileContentBuilder = new StringBuilder();
        if (file.exists())
        {
            String stringLine;
            try
            {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                while ((stringLine = bufferedReader.readLine()) != null)
                {
                    fileContentBuilder.append(stringLine).append("\n");
                }

                bufferedReader.close();
                fileReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return fileContentBuilder.toString();
    }
}
