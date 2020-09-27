package com.jm.online_store.config;

import com.jm.online_store.model.Address;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.Description;
import com.jm.online_store.model.News;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.SentStockService;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.TaskSettingsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * класс первичного заполнения таблиц.
 * <p>
 * для первичного заполнения базы данных раскомментировать аннотацию
 * "@PostConstruct" и поменять значение  ключа "spring.jpa.hibernate.ddl-auto"
 * в файле "application.yml" с "update" на "create" или "create-drop".
 */
@AllArgsConstructor
@Component
@Data
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;
    private final CategoriesService categoriesService;
    private final ProductService productService;
    private final NewsService newsService;
    private final OrderService orderService;
    private final ProductInOrderService productInOrderService;
    private final BasketService basketService;
    private final StockService stockService;
    private final SharedStockService sharedStockService;
    private final AddressService addressService;
    private final SentStockService sentStockService;
    private final TaskSettingsService taskSettingsService;
    private final CommonSettingsService commonSettingsService;

    /**
     * Основной метод для заполнения базы данных.
     * Вызов методов добавлять в этод метод.
     * Следить за последовательностью вызова.
     */
    //@PostConstruct
    public void initDataBaseFilling() {
        roleInit();
        newsInit();
        productInit();
        ordersInit();
        stockInit();
        sharedStockInit();
        addressInit();
        sentStockInit();
        paginationNewsAndStocksInit();
        taskSettingsInit();
        commonSettingsInit();
    }

    /**
     * Метод конфигурирования и первичного заполнения таблиц:
     * ролей, юзеров и корзины.
     */
    private void roleInit() {
        Role adminRole = new Role("ROLE_ADMIN");
        Role customerRole = new Role("ROLE_CUSTOMER");
        Role managerRole = new Role("ROLE_MANAGER");

        roleService.addRole(adminRole);
        roleService.addRole(customerRole);
        roleService.addRole(managerRole);

        User admin = new User("admin@mail.ru", "1");
        User manager = new User("manager@mail.ru", "1");
        User customer = new User("customer@mail.ru", "1");

        Optional<Role> admnRole = roleService.findByName("ROLE_ADMIN");
        Optional<Role> custRole = roleService.findByName("ROLE_CUSTOMER");
        Optional<Role> managRole = roleService.findByName("ROLE_MANAGER");

        Set<Role> customerRoles = new HashSet<>();
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> managerRoles = new HashSet<>();

        customerRoles.add(custRole.get());
        adminRoles.add(admnRole.get());
        adminRoles.add(custRole.get());
        managerRoles.add(managRole.get());

        manager.setRoles(managerRoles);
        admin.setRoles(adminRoles);
        customer.setRoles(customerRoles);

        userService.addUser(manager);
        userService.addUser(customer);
        userService.addUser(admin);

        Product product_1 = new Product("apple", 100000D, 10, 0.1);
        Product product_2 = new Product("samsung", 80000D, 100, 0.9);
        Product product_3 = new Product("xiaomi", 30000D, 50, 0.5);

        productService.saveProduct(product_1);
        productService.saveProduct(product_2);
        productService.saveProduct(product_3);

        Set<Product> productSet = new HashSet<>();
        productSet.add(product_1);
        productSet.add(product_2);
        productSet.add(product_3);

        customer = userService.findByEmail("customer@mail.ru").get();
        customer.setFavouritesGoods(productSet);
        userService.updateUser(customer);

        SubBasket subBasket_1 = new SubBasket();
        subBasket_1.setProduct(product_1);
        subBasket_1.setCount(1);
        basketService.addBasket(subBasket_1);
        SubBasket subBasket_2 = new SubBasket();
        subBasket_2.setProduct(product_3);
        subBasket_2.setCount(1);
        basketService.addBasket(subBasket_2);
        List<SubBasket> subBasketList = new ArrayList<>();
        subBasketList.add(subBasket_1);
        subBasketList.add(subBasket_2);
        customer.setUserBasket(subBasketList);
        userService.updateUser(customer);

        Random random = new Random();
        for (int i = 1; i < 20; i++) {
            userService.addUser(new User("customer" + i + "@mail.ru",
                    User.DayOfWeekForStockSend.values()[random.nextInt(6)],
                    String.valueOf(i)));
        }
    }

    /**
     * Метод первичного тестового заполнения новостей.
     */
    private void newsInit() {
        News firstNews = News.builder()
                .title("Акция от XP-Pen: Выигай обучение в Skillbox!")
                .anons("Не пропустите розыгрыш потрясающих призов.")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0);" +
                        " font-size: 1rem;\">Если вы любите создавать и повсюду ищите вдохновение, то следующая" +
                        " новость для вас!</b><br></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left:" +
                        " 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial," +
                        " sans-serif;\">XP-Pen проводят акицию с невроятно крутым призовым фоном, вы можете выиграть" +
                        " один из сертификатов на годовое обучение 2D или 3D рисованию в Skillbox, а также фирменные" +
                        " сувениры от бренда.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px;" +
                        " padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial," +
                        " sans-serif;\">Что нужно делать?</p><ul style=\"margin-right: 0px; margin-bottom: 0px;" +
                        " margin-left: 0px; padding: 0px; list-style-type: none; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\"><li style=\"margin: 0px; padding: 0px;\">1.Купить" +
                        " в <b>Online-Shop</b> любой графический планшет или интерактивный дисплей XP-Pen с 15" +
                        " августа по 15 сентября 2020 года.</li><li style=\"margin: 0px; padding: 0px;\">2.Пришлите" +
                        " серийный номер изделия на эл. почту sales_ru@xp-pen.com</li>X – XP-Pen подведут итоги" +
                        " методом рандома, так что шанс есть у каждого!</li></ul><p style=\"margin-right: 0px;" +
                        " margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\">Вы только взгляните на эти призы!</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">1 сертификат на" +
                        " обучение в школе SkillBox по курсу «Профессия 2D-художник»</p><p style=\"margin-right:" +
                        " 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на обучение в школе SkillBox по" +
                        " курсу «Профессия 3D-художник»</p><p style=\"margin-right: 0px; margin-bottom: 1em;" +
                        " margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;," +
                        " Arial, sans-serif;\">5 наборов фирменных сувениров от XP-Pen (в набор входит рюкзачок" +
                        " XP-Pen, брелок с фирменным персонажем XP-Pen лисенком Фениксом и чехол для пера)</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на 50%" +
                        " скидку на обучение в школе SkillBox по курсу «Профессия 2D-художник»</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">3 сертификата на" +
                        " 50% скидку на обучение в школе SkillBox по курсу «Профессия 3D-художник»</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "Online-shop желает всем удачи!</p>")
                .postingDate(LocalDateTime.now())
                .archived(true)
                .build();

        News secondNews = News.builder()
                .title("Акция от AORUS: Играй и смотри!")
                .anons("Купите монитор и получите целый год фильмов с ivi и вкусную пиццу в подарок.")
                .fullText("<h2 style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"\"><font color=\"#ff0000\">" +
                        "Хорошие новости в Online-Shop!</font></b></h2><p style=\"margin-right: 0px; margin-bottom:" +
                        " 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;," +
                        " Arial, sans-serif;\"><span style=\"background-color: rgb(0, 255, 0);\">Смотреть кино стало" +
                        " еще интереснее и вкуснее.</span> При покупке одного из мониторов AORUS вы получаете в" +
                        " подарок 12 месяцев подписки на ivi и промокод на 1200 рублей в Додо-пицца. Акция продлится" +
                        " с 10 по 31 августа.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px;" +
                        " padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<i style=\"font-size: 1rem;\">Приятных покупок в Online-Shop!</i></p><p style=\"margin-right:" +
                        " 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\"><i style=\"font-size: 1rem;\">23<br></i><br></p>")
                .postingDate(LocalDateTime.now().minusDays(5L))
                .archived(false)
                .build();

        News thirdNews = News.builder()
                .title("Сегодня стартует предзаказ на флагманские продукты Samsung!")
                .anons("Сделайте предзаказ и получите подарок.")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Сегодня стартует предзаказ на новые флагманские продукты Samsung!<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDateTime.now().minusDays(13L))
                .archived(false)
                .build();

        newsService.save(firstNews);
        newsService.save(secondNews);
        newsService.save(thirdNews);
    }

    /**
     * Метод первичного тестового заполнения товаров.
     */
    private void productInit() {

        Categories category1 = new Categories("Ноутбуки", "Компьютеры");
        Categories category2 = new Categories("Компьютеры", "Компьютеры");
        Categories category3 = new Categories("Смартфоны", "Смартфоны и гаджеты");
        Categories category4 = new Categories("Комплектующие", "Компьютеры");
        Categories category5 = new Categories("Периферия", "Компьютеры");
        Categories category6 = new Categories("Планшеты", "Смартфоны и гаджеты");
        Categories category7 = new Categories("Электронные книги", "Смартфоны и гаджеты");
        Categories category8 = new Categories("Аксессуары", "Смартфоны и гаджеты");
        Categories category9 = new Categories("Телевизоры", "ТВ и развлечения");
        Categories category10 = new Categories("Игры", "ТВ и развлечения");
        Categories category11 = new Categories("Аудиотехника", "ТВ и развлечения");
        Categories category12 = new Categories("Оргтехника", "Офис и сеть");
        Categories category13 = new Categories("Роутеры и сетевое оборудование", "Офис и сеть");
        Categories category14 = new Categories("Техника для кухни", "Бытовая техника");

        Product product1 = new Product("Asus-NX4567", 299.9, 15, 4.0, "Computer", false);
        Product product2 = new Product("ACER-543", 399.9, 10, 4.2, "Computer", false);
        Product product3 = new Product("Samsung-7893", 259.9, 20, 4.6, "Computer", false);

        Product product4 = new Product("NX-7893-PC-09878", 924.0, 3, 4.2, "Computer", false);
        Product product5 = new Product("ZX-7654-PC-1", 1223.9, 7, 4.7, "Computer", false);
        Product product6 = new Product("NY-2345-PC-453", 1223.9, 7, 4.7, "Computer", false);

        Product product7 = new Product("XIAOMI-Mi10", 599.9, 120, 4.9, "Cellphone", false);
        Product product8 = new Product("LG-2145", 439.5, 78, 3.9, "Cellphone", false);
        Product product9 = new Product("Apple-10", 1023.9, 74, 4.8, "Cellphone", false);

        Product product10 = new Product("Notebook 1", 99.9, 2, 0.0, "Computer");
        Product product11 = new Product("Notebook 2", 99.9, 2, 0.0, "Computer");
        Product product12 = new Product("Notebook 3", 99.9, 2, 0.0, "Computer");

        Description description1 = new Description("12344232", "ASUS", 2, "500x36x250", "black", 1.3, "Оснащенный 15.6-дюймовым экраном ноутбук ASUS TUF Gaming FX505DT-AL087 – игровой портативный компьютер, который ничто не помешает вам использовать и в роли универсального домашнего компьютера.");
        Description description2 = new Description("23464223", "ACER", 1, "654x38x245", "yellow", 2.1, "some additional info here");
        Description description3 = new Description("99966732", "Samsung", 3, "550x27x368", "white", 1.1, "some additional info here");
        Description description4 = new Description("33311432NXU", "ATop corp.", 3, "698x785x368", "black", 3.1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris id condimentum tortor. Aliquam tristique tempus ipsum id laoreet. Pellentesque ligula lectus, finibus eget auctor pellentesque, molestie ac elit. Fusce in maximus leo. Morbi maximus vel enim");
        Description description5 = new Description("33211678NXU", "ATop corp.", 3, "690x765x322", "black", 3.5, "some additional info here");
        Description description6 = new Description("333367653Rh", "Rhino corp.", 3, "612x678x315", "orange", 2.8, "some additional info here");
        Description description7 = new Description("X54355543455", "Xiaomi", 1, "115x56x13", "grey", 0.115, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris id condimentum tortor. Aliquam tristique tempus ipsum id laoreet. Pellentesque ligula lectus, finibus eget auctor pellentesque, molestie ac elit. Fusce in maximus leo. Morbi maximus vel enim", 512, 512, "1920x960", true, "5.0");
        Description description8 = new Description("L55411165632", "LG", 2, "110x48x19", "black", 0.198, "some additional info here", 1024, 256, "1920x960", false, "4.0");
        Description description9 = new Description("A88563902273", "Apple corp.", 1, "112x55x8", "black", 0.176, "some additional info here", 2048, 128, "1024x480", true, "5.0");

        product1.setDescriptions(description1);
        product2.setDescriptions(description2);
        product3.setDescriptions(description3);
        product4.setDescriptions(description4);
        product5.setDescriptions(description5);
        product6.setDescriptions(description6);
        product7.setDescriptions(description7);
        product8.setDescriptions(description8);
        product9.setDescriptions(description9);
        product10.setDescriptions(description1);
        product11.setDescriptions(description1);
        product12.setDescriptions(description1);

        category1.setProducts(Arrays.asList(product1, product2, product3, product10, product11, product12));
        category2.setProducts(Arrays.asList(product4, product5, product6));
        category3.setProducts(Arrays.asList(product7, product8, product9));

        categoriesService.saveAll(Arrays.asList(category1, category2, category3,
                category4, category5, category6, category7, category8, category9, category10, category11, category12, category13, category14));
    }

    /**
     * Метод первичного тестового заполнения заказов.
     */
    private void ordersInit() {
        User customer = userService.findByEmail("customer@mail.ru").get();

        List<Long> productsIds = new ArrayList<>();
        productsIds.add(productService.findProductByName("NX-7893-PC-09878").get().getId());
        productsIds.add(productService.findProductByName("Asus-NX4567").get().getId());
        productsIds.add(productService.findProductByName("ACER-543").get().getId());
        productsIds.add(productService.findProductByName("XIAOMI-Mi10").get().getId());
        productsIds.add(productService.findProductByName("LG-2145").get().getId());
        productsIds.add(productService.findProductByName("Apple-10").get().getId());

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(LocalDateTime.of(2019, 12, 31, 22, 10), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 1, 23, 13, 37), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 3, 10, 16, 51), Order.Status.INCARTS));
        orders.add(new Order(LocalDateTime.of(2020, 6, 13, 15, 3), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.now(), Order.Status.INCARTS));

        List<Long> ordersIds = new ArrayList<>();
        for (Order order : orders) {
            ordersIds.add(orderService.addOrder(order));
        }

        productInOrderService.addToOrder(productsIds.get(0), ordersIds.get(0), 1);
        productInOrderService.addToOrder(productsIds.get(1), ordersIds.get(0), 2);
        productInOrderService.addToOrder(productsIds.get(2), ordersIds.get(1), 1);
        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(2), 2);
        productInOrderService.addToOrder(productsIds.get(3), ordersIds.get(3), 1);
        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(3), 2);
        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(3), 3);
        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(4), 3);
        customer.setOrders(Set.copyOf(orderService.findAll()));
        userService.updateUser(customer);
    }

    /**
     * Метод первичного тестового заполнения акций.
     */
    private void stockInit() {
        Stock firstStock = Stock.builder()
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(12L))
                .stockTitle("Команда Online-Store сообщает о начале акции – «Рассрочка или бонусы! HD-" +
                        " и UltraHD-телевизоры Samsung»")
                .stockText("<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;," +
                        " Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "Телевизоры Samsung&nbsp;</b><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT" +
                        " Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\">обеспечивают четкое, реалистичное изображение и точную цветопередачу." +
                        " Вы сможете разглядеть мельчайшие детали транслируемой картинки и насладиться объемным " +
                        "звучанием, которое можно настроить под любой контент. Технологии Smart TV, которыми " +
                        "обладают почти все модели из списка, делают из телевизора настоящий мультимедийный " +
                        "центр с доступом к различным фильмам, сериалам и передачам, мессенджерам и соцсетям." +
                        "</span><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\"><span " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, " +
                        "sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">Оформите " +
                        "беспроцентный кредит</span><span style=\"position: relative; font-size: 12px; " +
                        "line-height: normal; vertical-align: baseline; top: -0.5em; color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; letter-spacing: " +
                        "0.23px; text-align: start;\">1</span><span style=\"color: rgb(51, 51, 51); font-family:" +
                        " &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">&nbsp;на&nbsp;</span><b style=\"font-weight: bold; color:" +
                        " rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">телевизоры Samsung</b>" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;" +
                        "из списка в любом магазине нашей сети или получите до 70 000 рублей на бонусную карту " +
                        "ProZaPass</span><span style=\"position: relative; font-size: 12px; line-height: normal;" +
                        " vertical-align: baseline; top: -0.5em; color: rgb(51, 51, 51); font-family: &quot;PT" +
                        " Sans&quot;, Helvetica, Arial, sans-serif; letter-spacing: 0.23px; text-align: start;\">" +
                        "2</span><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica," +
                        " Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;–" +
                        " выбор за вами!</span><br>")

                .build();

        Stock secondStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(5L))
                .endDate(LocalDate.now().plusDays(3L))
                .stockTitle("Команда Online-Store сообщает о начале акции – «Выгодный онлайн-шопинг с Visa!»")
                .stockText("<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">С " +
                        "Visa за скидками далеко ходить не надо! Visa и DNS подготовили для вас выгодное " +
                        "предложение – оплачивая покупки онлайн картами Visa в период с 15 сентября по 15 " +
                        "ноября 2020 года вы получаете «Специальные цены» на выборочный ассортимент.</span>" +
                        "<br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "<br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">Что надо сделать:</b><br style=\"color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><span style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">1. Выберите товар из списка, " +
                        "участвующих в акции.</span><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT" +
                        " Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\"><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\">2. Добавьте выбранный товар в корзину.</span><br style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\"><span style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">3. Нажмите «Получить " +
                        "скидку за оплату картой Visa» при оформлении заказа.</span><br style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif;" +
                        " font-size: 18px; letter-spacing: 0.23px; text-align: start;\"><span style=\"color:" +
                        " rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">4. Выберите способ " +
                        "оплаты «Оплатить онлайн» – «Банковская карта».</span><br style=\"color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><b style=\"font-weight: bold; color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">ВАЖНО!</b><span " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "</span><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: " +
                        "start;\"><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: " +
                        "start;\"><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: " +
                        "start;\">Скидка будет предоставлена только в том случае, если вы оплачиваете товар " +
                        "картой платежной системы Visa.</span><br style=\"color: rgb(51, 51, 51); font-family: " +
                        "&quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\"><span style=\"color: rgb(51, 51, 51); font-family: " +
                        "&quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">Если в заказ будет добавлена: дополнительная гарантия, " +
                        "услуга или опциональный товар, то воспользоваться скидкой не возможно.</span><br " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, " +
                        "sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\">Юридические лица и индивидуальные предприниматели " +
                        "не вправе принимать участие в акции.</span><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px;" +
                        " letter-spacing: 0.23px; text-align: start;\">")
                .build();

        Stock thirdStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(20L))
                .endDate(LocalDate.now().minusDays(5L))
                .stockTitle("Команда DNS сообщает о начале акции – «Рассрочка или бонусы! Инверторные холодильники LG»")
                .stockText("<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial," +
                        " sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">Благодаря " +
                        "инверторным компрессорам</span><b style=\"font-weight: bold; color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">&nbsp;холодильники LG&nbsp;</b><span " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, " +
                        "sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">работают " +
                        "практически бесшумно. Технология No Frost позволит вам не тратить время и силы на " +
                        "разморозку и ограничит образование плесени и неприятных запахов внутри камер. " +
                        "Вместительные и стильные&nbsp;</span><b style=\"font-weight: bold; color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">холодильники</b><span style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;</span>" +
                        "<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">LG</b><span style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: " +
                        "18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;с классами энергопотребления " +
                        "A+ и A++ (в зависимости от модели)помогут сохранить ваши запасы свежими продолжительное " +
                        "время и украсят интерьер вашей кухни.</span><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><span style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">Оформите беспроцентный кредит</span>" +
                        "<span style=\"position: relative; font-size: 12px; line-height: normal; vertical-align:" +
                        " baseline; top: -0.5em; color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; letter-spacing: 0.23px; text-align: start;\">1</span>" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica," +
                        " Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "&nbsp;на&nbsp;</span><b style=\"font-weight: bold; color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px;" +
                        " letter-spacing: 0.23px; text-align: start;\">инверторные</b><span style=\"color:" +
                        " rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif;" +
                        " font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;</span>" +
                        "<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">холодильники LG</b><span style=\"color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">&nbsp;из списка в любом магазине нашей" +
                        " сети или получите 10% от стоимости покупки на бонусную карту ProZaPass</span>" +
                        "<span style=\"position: relative; font-size: 12px; line-height: normal; vertical-align:" +
                        " baseline; top: -0.5em; color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; letter-spacing: 0.23px; text-align: start;\">2</span>" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica," +
                        " Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "&nbsp;– выбор за вами!</span>")
                .build();

        stockService.addStock(firstStock);
        stockService.addStock(secondStock);
        stockService.addStock(thirdStock);
    }

    public void sharedStockInit() {
        String[] socialNetworkNames = {"facebook", "vk", "twitter"};
        List<Stock> stocks = stockService.findAll();
        List<User> users = userService.findAll();
        Long firstNumber = stocks.get(0).getId();
        Long lastNumber = stocks.get(stocks.size() - 1).getId();
        Random random = new Random();
        for (Stock stock : stocks) {
            for (User user : users) {
                long generatedLongForStock = firstNumber + (long) (Math.random() * (lastNumber - firstNumber));
                SharedStock sharedStock = SharedStock.builder()
                        .user(user)
                        .stock(stockService.findStockById(generatedLongForStock))
                        .socialNetworkName(socialNetworkNames[random.nextInt(socialNetworkNames.length)])
                        .build();
                sharedStockService.addSharedStock(sharedStock);
            }
        }

    }

    /**
     * Метод первичного заполнения акций, которые были отправлены пользователям
     */
    public void sentStockInit() {
        Random random = new Random();
        List<Stock> stocks = stockService.findAll();
        List<User> users = userService.findAll();

        for (int i = 0; i < 20; i++) {
            sentStockService.addSentStock(SentStock.builder().user(users.get(random.nextInt(users.size())))
                    .stock(stocks.get(random.nextInt(stocks.size())))
                    .sentDate(LocalDate.now().plusDays(random.nextInt(8)))
                    .build());
        }
    }

    /**
     * Метод инициализации новостей и акций в профиле менеджера для тестирования динамической пагинации.
     */
    public void paginationNewsAndStocksInit() {
        for (int i = 0; i < 50; i++) {
            News news = News.builder()
                    .title(i + " Сегодня стартует предзаказ на флагманские продукты Samsung!")
                    .anons("Сделайте предзаказ и получите подарок.")
                    .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                            "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                            "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                            " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                            " font-size: 16px;\">Сегодня стартует предзаказ на новые флагманские продукты Samsung!<b></b>" +
                            "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                            " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                            " font-size: 16px;\"><br></p></h1>")
                    .postingDate(LocalDateTime.now().minusDays(Math.round(Math.random() * 20)))
                    .archived(false)
                    .build();
            newsService.save(news);
        }

        for (int i = 0; i < 50; i++) {
            Stock stock = Stock.builder()
                    .startDate(LocalDate.now().minusDays(20L))
                    .endDate(LocalDate.now().minusDays(5L))
                    .stockTitle("Скидки на игры ЕА!")
                    .stockText("В течение действия акции вы можете приобрести игры ЕА из списка по" +
                            " очень привлекательным ценам!" +
                            "Вы можете стать обладателем игр EA для Xbox One, Nintendo Switch и PS4" +
                            " в различных жанрах. Ощутите всю радость победы в хоккейном матче, станьте" +
                            " стремительным уличным автогонщиком, постройте дом мечты или очутитесь в" +
                            " фантастическом мире и примите участие в битве галактических масштабов!")
                    .build();
            stockService.addStock(stock);
        }
    }
    /**
     * Метод первичной инициалзации адресов, 2 адреса для магазина и 1 адрес прикрепляется к заказу
     */
    private void addressInit() {
        Address address1 = new Address("420077","Татарстан","Казань","Революционная","25",true);
        Address address2 = new Address("420078","Московская область","Москва","Ленина","126",true);
        Address address3 = new Address("420079","Тамбовская область","Тамбов","Запорожская","11",false);
        Address address4 = new Address("420080","Тамбовская область","Тамбов","Запорожская","12",false);
        addressService.addAddress(address1);
        addressService.addAddress(address2);
        addressService.addAddress(address3);
        addressService.addAddress(address4);
        Address addressToAdd = addressService.findAddressById(3L).get();
        User userToUpdate = userService.findByEmail("customer@mail.ru").get();
        userToUpdate.setUserAddresses(Collections.singleton(addressToAdd));
        userService.updateUser(userToUpdate);
        userToUpdate = userService.findByEmail("customer@mail.ru").get();
        Set<Address> userAdresses = userToUpdate.getUserAddresses();
        System.out.println();
        userAdresses.add(addressService.findAddressById(4L).get());
        userToUpdate.setUserAddresses(userAdresses);
        userService.updateUser(userToUpdate);
        Order order = orderService.findOrderById(5L).get();
        order.setAddress(addressService.findAddressById(3L).get());
        orderService.updateOrder(order);
    }
    /**
     * ini method for email stock distribution settings.
     * creates task for stock distribution with status not active
     * and current time
     */
    public void taskSettingsInit() {
        TaskSettings taskSettings = TaskSettings.builder()
                .taskName("stockMailDistribution")
                .active(false)
                .startTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        taskSettingsService.addNewTaskSetting(taskSettings);
    }

    /**
     * init method for email stock distribution template
     * creates template for email.
     */
    public void commonSettingsInit() {
        CommonSettings emailStockDistributionTemplate = CommonSettings.builder()
                .settingName("stock_email_distribution_template")
                .textValue("<p>Уважаемый @@user@@, спешим сообщить вам о новых Акциях!" +
                        "</p><p>@@stockList@@</p><p>С Уважением</p><p>Online-store.ru</p>")
                .build();
        commonSettingsService.addSetting(emailStockDistributionTemplate);
    }
}
