package model;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;

public class DataModel
{
//    public record Dataset(String name, List<Integer> values) {}
    private final List<Integer> data = new ArrayList<>();

    //读取csv文件
    public void readCSV(String filePath) throws IOException
    {
        data.clear();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        String line;
        while((line = br.readLine()) != null)
        {
            line = line.replace("\uFEFF", ""); // 去除BOM字符
            String[] nums = line.split(",");
            for (String num : nums)
            {
                data.add((int) Double.parseDouble(num.trim()));
            }
        }
        br.close();
    }

    public List<Integer> getData()
    {
        return data;
    }

    public void setData(List<Integer> list)
    {
        data.clear();
        data.addAll(list);
    }

    public void quickSort()
    {
        Collections.sort(data);
    }
}