package client;


import com.google.protobuf.ServiceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class Client {

    public Configuration conf() throws IOException, ServiceException {
        Configuration config = HBaseConfiguration.create();
        config.clear();
        // config.setInt("timeout", 12000);
        //config.set("zookeeper.znode.parent", "/hbase");
        config.set("hbase.zookeeper.quorum", "myhbase");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        //config.set("hbase.master", "127.0.0.1:16000");
        HBaseAdmin.checkHBaseAvailable(config);


        return config;
    }


    public static void main(String[] args) throws IOException, ServiceException {
        Client client = new Client();
        Configuration conf = client.conf();
        //client.isExists(conf);
        client.populate(conf);
client.scaner(conf);
    }

    public void isExists(Configuration conf) throws IOException {
        boolean bool;
        TableName table = TableName.valueOf("user");

        try (Connection connection = ConnectionFactory.createConnection(conf);
             Admin admin = connection.getAdmin()) {
            bool = admin.tableExists(table);

            System.out.println(bool);


        }
    }

    public void populate(final Configuration conf) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(conf)) {

            TableName tableName = TableName.valueOf("user");
            Table table = connection.getTable(tableName);

            Put p = new Put(Bytes.toBytes("row2"));
            //Customer table has personal and address column families. So insert data for 'name' column in 'personal' cf
            // and 'city' for 'address' cf
            p.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("name"), Bytes.toBytes("Riki"));
            p.addColumn(Bytes.toBytes("prof"), Bytes.toBytes("type"), Bytes.toBytes("advokat1"));
           // p.addColumn(Bytes.toBytes("prof"), Bytes.toBytes("sale"), Bytes.toBytes("300"));
            table.put(p);
            Get get = new Get(Bytes.toBytes("row1"));
            Result result = table.get(get);
            byte[] name = result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("name"));
            byte[] city = result.getValue(Bytes.toBytes("prof"), Bytes.toBytes("type"));
            System.out.println("Name: " + Bytes.toString(name) + " City: " + Bytes.toString(city));
            table.close();
        }
    }

    public void scaner(final Configuration conf) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            TableName tableName = TableName.valueOf("user");
            Table table = connection.getTable(tableName);
            Scan scan = new Scan();
            // Scanning the required columns
            scan.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("name"));

            ResultScanner scanner = table.getScanner(scan);

            // Reading values from scan result
            for (Result result = scanner.next(); result != null; result = scanner.next()) {
                System.out.println("Found row : " + result);

            }

        }
    }


}
