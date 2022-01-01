package com.training.transformation;

import com.training.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class ConnTransformationJoinPersonOrderProduct {

    public static void main(String[] args) throws Exception {

        log.info("data {}", ConnTransformationJoinPersonOrderProduct.class.getName());
        ParameterTool params = ParameterTool.fromArgs(args);

        String endpoint = params.get("endpoint", "hadoop1:9092");
        String topicOfPerson = params.get("topic_of_person", "flink_person");
        String topicOfOrder = params.get("topic_of_order", "flink_order");
        String topicOfProduct = params.get("topic_of_product", "flink_product");

        Properties props = new Properties();

        // 构造消费者参数
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "flink_person");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "flink_person_connect");
        log.info("props:{}", props);

        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        // 构建消费者
        FlinkKafkaConsumer010<Person> personConsumer = new FlinkKafkaConsumer010<>(topicOfPerson, new PersonSchema(), props);
        FlinkKafkaConsumer010<Order> orderConsumer = new FlinkKafkaConsumer010<>(topicOfOrder, new OrderSchema(), props);
        FlinkKafkaConsumer010<Product> productConsumer = new FlinkKafkaConsumer010<>(topicOfProduct, new ProductSchema(), props);

        // 添加数据源
        DataStreamSource<Person> personSource = env.addSource(personConsumer, "person_source");
        DataStreamSource<Product> productSource = env.addSource(productConsumer, "product_source");
        DataStreamSource<Order> orderSource = env.addSource(orderConsumer, "order_source");

        // connect 两个数据源
        SingleOutputStreamOperator<PersonOrder> personOrderProcess = personSource.connect(orderSource).process(new CoProcessFunction<Person, Order, PersonOrder>() {
            HashMap<String, Person> personContainer = new HashMap();
            HashMap<String, Order> orderContainer = new HashMap();

            @Override
            public void processElement1(Person value, Context ctx, Collector<PersonOrder> out) throws Exception {
                log.info("data:{}", value);
                personContainer.put(value.getId(), value);
                if (!orderContainer.containsKey(value.getId())) {
                    return;
                }
                Order order = orderContainer.get(value.getId());
                out.collect(new PersonOrder(value.getId(), order.getId(), order.getProductId(), order.getPrice(), order.getPushedTime(), order.getPushedTime(), order.getFinishedTime()));
            }

            @Override
            public void processElement2(Order value, Context ctx, Collector<PersonOrder> out) throws Exception {
                log.info("data:{}", value);

                orderContainer.put(value.getId(), value);
                if (!personContainer.containsKey(value.getUserId())) {
                    return;
                }
                Person person = personContainer.get(value.getId());
                out.collect(new PersonOrder(person.getId(), value.getId(), value.getProductId(), value.getPrice(), value.getPushedTime(), value.getPushedTime(), value.getFinishedTime()));
            }
        });

        // connect 数据源
        SingleOutputStreamOperator<UserOrderProduct> afterJoin = personOrderProcess.connect(productSource).process(new CoProcessFunction<PersonOrder, Product, UserOrderProduct>() {
            HashMap<String, Product> productContainer = new HashMap<>();
            HashMap<String, PersonOrder> personOrderContainer = new HashMap<String, PersonOrder>();

            @Override
            public void processElement1(PersonOrder value, Context ctx, Collector<UserOrderProduct> out) throws Exception {
                log.info("data:{}", value);
                personOrderContainer.put(value.getProductId(), value);
                if (!productContainer.containsKey(value.getProductId())) {
                    return;
                }

                Product product = productContainer.get(value.getProductId());

                out.collect(new UserOrderProduct(value.getUserId(), value.getOrderId(), value.getProductId(), product.getName(), product.getPrice(), value.getOrderTime(), value.getPushedTime(), value.getFinishedTime()));
            }

            @Override
            public void processElement2(Product value, Context ctx, Collector<UserOrderProduct> out) throws Exception {
                log.info("data.processElement2:{}", value);

                productContainer.put(value.getId(), value);
                if (!personOrderContainer.containsKey(value.getId())) {
                    return;
                }

                PersonOrder po = personOrderContainer.get(value.getId());
                out.collect(new UserOrderProduct(po.getUserId(), po.getOrderId(), po.getProductId(), value.getName(), value.getPrice(), po.getOrderTime(), po.getPushedTime(), po.getFinishedTime()));
            }
        });

        // 禁用优化
        afterJoin.disableChaining();

        // data sink
        afterJoin.print();

        // 执行 Flink 程序
        env.execute("connect_join_user_order_product");
    }
}
