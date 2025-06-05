package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取 CSV（单列或多列）中的整数，遇到非数字会直接跳过
 */
public class CsvUtils
{
    /**
     * 把文件里所有整数读进 List<Integer>
     @param filePath 绝对 / 相对路径
     */
    public static List<Integer> readInts(String filePath) throws IOException
    {
        List<Integer> list = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Path.of(filePath)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                // 允许逗号 / 空格 / 制表符分隔
                for (String token : line.split("[,\\s]+"))
                {
                    if (!token.isBlank())
                    {
                        try
                        {
                            list.add(Integer.parseInt(token.trim()));
                        }
                        catch (NumberFormatException ignore) { /* 跳过非整数 */ }
                    }
                }
            }
        }
        return list;
    }
}
