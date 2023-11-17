import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            int a;
            // Укажите путь к вашему текстовому файлу с кодом
            String filePath = "C:\\Users\\User\\Desktop\\Main.txt";

            // Считываем код из файла
            StringBuilder code = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append("\n");
            }
            reader.close();

            // Сохраняем код в файл
            String className = "DynamicClass";
            String fileName = className + ".java";
            Path filePathJava = Paths.get(fileName);
            Files.write(filePathJava, Arrays.asList(code.toString().split("\n")));

            // Компилируем код
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int compilationResult = compiler.run(null, null, null, fileName);

            if (compilationResult == 0) {
                // Загружаем и выполняем код
                DynamicClassLoader classLoader = new DynamicClassLoader();
                Class<?> dynamicClass = classLoader.loadClass(className);
                if (dynamicClass != null && Runnable.class.isAssignableFrom(dynamicClass)) {
                    Runnable runnable = (Runnable) dynamicClass.getDeclaredConstructor().newInstance();
                    runnable.run();
                } else {
                    System.out.println("Класс не реализует интерфейс Runnable");
                }

            } else {
                System.out.println("Компиляция не удалась");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DynamicClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] classData = Files.readAllBytes(Paths.get(name + ".class"));
                return defineClass(name, classData, 0, classData.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name);
            }
        }
    }
}
