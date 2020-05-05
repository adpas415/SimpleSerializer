package adp.io;

import java.io.*;

public class SimpleSerializer {

    public static byte[] serializeObject(Serializable object) throws Exception {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ObjectOutputStream out = new ObjectOutputStream(bos)) {

            out.writeObject(object);
            out.flush();

            return bos.toByteArray();

        }
    }

    public static Object deserializeObject(ClassLoader customClassLoader, byte[] bytes) throws Exception {

        class CustomObjectInputStream extends ObjectInputStream {

            ClassLoader classLoader;

            public CustomObjectInputStream(ClassLoader customClassLoader, InputStream in) throws IOException {
                super(in);
                this.classLoader = customClassLoader;
            }

            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                try{

                    return Class.forName(desc.getName(), false, classLoader);

                } catch(ClassNotFoundException e){
                    return super.resolveClass(desc);
                }
            }

        }

        try( ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new CustomObjectInputStream(customClassLoader, bis)) {

            return ois.readObject();

        }

    }

    public static Object deserializeObject(byte[] bytes) throws Exception {

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            return ois.readObject();

        }

    }

    public static void exportBytesToFile(byte[] toExport, String dirPath, String fileName) throws Exception {

        String filePath = dirPath+fileName;

        File dir = new File(dirPath);

        if (!dir.exists())
            dir.mkdirs();

        File file = new File(filePath);

        if(!file.exists())
            file.createNewFile();

        try(FileOutputStream fos = new FileOutputStream(filePath)) {

            fos.write(toExport);

        }

    }

    public static byte[] importBytesFromFile(String filePath) throws Exception {

        try (FileInputStream fis = new FileInputStream(filePath)) {

            return fis.readAllBytes();

        }

    }

    public static void exportObjectToFile(Serializable toExport, String dirPath, String fileName) throws Exception {

        exportBytesToFile(serializeObject(toExport), dirPath, fileName);

    }

    public static Object importObjectFromFile(String filePath) throws Exception {

        return deserializeObject(importBytesFromFile(filePath));

    }

}
