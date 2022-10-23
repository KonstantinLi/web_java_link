package com.kpi.web;

import com.kpi.web.controller.AdminController;
import com.kpi.web.controller.RecordType;
import com.kpi.web.controller.SortingType;
import com.kpi.web.controller.UserController;
import com.kpi.web.model.Activity;
import com.kpi.web.model.Category;
import com.kpi.web.model.User;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.printf("----------------------------------------%n" +
                        "|%-38s|%n----------------------------------------%n",
                StringUtils.center("Вітаємо в системі \"Облік часу\"", 38));

        auth:
        while (true) {
            System.out.print("Для початку оберіть свою роль (адміністратор - 1, користувач - 2):\n");

            Scanner scanner = new Scanner(System.in);
            int role;

            while (true) {
                try {
                    role = scanner.nextInt();
                    if (role != 1 && role != 2) {
                        throw new Exception();
                    }
                    break;
                } catch (Exception ex) {
                    System.out.println("Будь ласка, введіть 1 або 2.");
                }
            }

            if (role == 1) {
                System.out.println("Ви бажаєте увійти в систему в якості адміністратора.");

                AdminController controller;
                while (true) {
                    System.out.println("Введіть логін:");
                    String login = scanner.next();

                    System.out.println("Введіть пароль:");
                    String password = scanner.next();

                    controller = AdminController.login(login, password);
                    if (controller == null) {
                        System.out.printf("Ви ввели неправильний логін \"%s\" або пароль \"%s\"\n", login, password);
                    } else {
                        System.out.printf("Адміністратор \"%s\" успішно авторизований.%n", login);
                        System.out.println("Для виходу із системи введіть exit");
                        break;
                    }
                }

                command:
                while (true) {
                    String command = scanner.nextLine();
                    String[] params = command.split(" ");

                    if (params[0].equals("list")) {
                        if (params.length >= 2) {
                            if (params[1].equals("users")) {
                                List<User> users = controller.get(User.class);
                                System.out.printf("-----------------------------------------%n" +
                                                "|%-5s|%-16s|%-16s|%n" +
                                                "-----------------------------------------%n",
                                        " id ",
                                        StringUtils.center("login", 16),
                                        StringUtils.center("password", 16));

                                for (User user : users) {
                                    System.out.printf("| %-4d| %-15s| %-15s| %n" +
                                                    "-----------------------------------------%n",
                                            user.getId(), user.getLogin(), user.getPassword());
                                }
                            } else if (params[1].equals("categories")) {
                                List<Category> categories = controller.get(Category.class);
                                System.out.printf("------------------------%n" +
                                                "|%-5s|%-16s|%n" +
                                                "------------------------%n",
                                        "id ", StringUtils.center("type", 16));

                                for (Category category : categories) {
                                    System.out.printf("| %-4d| %-15s|%n" +
                                                    "------------------------%n",
                                            category.getId(), category.getType());
                                }
                            } else if (params[1].equals("activities")) {
                                List<Activity> activities = controller.get(Activity.class);
                                SortingType sortingType = SortingType.ASCENDING;

                                if (params.length >= 3) {
                                    String[] keys = Arrays.copyOfRange(params, 2, params.length);
                                    if (Arrays.asList(keys).contains("--desc")) {
                                        sortingType = SortingType.DESCENDING;
                                    }

                                    for (int i = 0; i < keys.length; i++) {
                                        if (keys[i].equals("--sort")) {
                                            try {
                                                controller.sortActivities(activities, keys[i + 1], sortingType);
                                            } catch (Exception ex) {
                                                System.out.println("Параметр --sort має приймати такі значення: " +
                                                        "category, user, duration");
                                                System.out.println("Для ознайомлення з повним списком команд введіть help");
                                                continue command;
                                            }
                                        }
                                    }
                                }

                                System.out.printf("-----------------------------------------------------------------%n" +
                                                "|%-5s|%-16s|%-16s|%-11s|%-11s|%n" +
                                                "-----------------------------------------------------------------%n",
                                        " id ",
                                        StringUtils.center("category", 16),
                                        StringUtils.center("user", 16),
                                        StringUtils.center("duration", 11),
                                        StringUtils.center("confirmed", 11));

                                for (Activity activity : activities) {
                                    System.out.printf("| %-4d| %-15s| %-15s| %-10d| %-10b|%n" +
                                                    "-----------------------------------------------------------------%n",
                                            activity.getId(), activity.getCategory().getType(),
                                            activity.getUser().getLogin(),
                                            activity.getDuration(), activity.isConfirmed());
                                }
                            } else {
                                System.out.println("Команда list приймає наступні параметри: " +
                                        "users, categories, activities");
                                System.out.println("Для ознайомлення з повним списком команд введіть help");
                            }
                        } else {
                            System.out.println("Команда list повинна приймати щонайменше один параметр: " +
                                    "users, categories, activities");
                            System.out.println("Для ознайомлення з повним списком команд введіть help");
                        }
                    } else if (params[0].equals("add")) {
                        if (params.length >= 2) {
                            if (params[1].equals("user")) {
                                String[] keys = Arrays.copyOfRange(params, 2, params.length);
                                String login = null, password = null;

                                try {
                                    for (int i = 0; i < keys.length; i++) {
                                        if (keys[i].equals("-l") && !keys[i + 1].equals("-p")) {
                                            login = keys[i + 1];
                                        } else if (keys[i].equals("-p") && !keys[i + 1].equals("-l")) {
                                            password = keys[i + 1];
                                        }
                                    }

                                    if (login == null || password == null) {
                                        throw new Exception();
                                    }
                                } catch (Exception ex) {
                                    System.out.println("""
                                            Команда add user повинна мати наступний синатксис:
                                            add user -l <login> -p <password>""");
                                    continue command;
                                }

                                try {
                                    controller.addUser(login, password);
                                    System.out.printf("Користувач \"%s\" успішно доданий.", login);
                                } catch (RuntimeException ex) {
                                    System.out.println("""
                                            Не вдалося додати нового користувача.
                                            Можливо, користувач з такими ім'ям вже існує.
                                            Будь ласка, придумайте новий логін або відправте запит через декілька хвилин.""");
                                    continue command;
                                }

                                System.out.printf("Користувач \"%s\" успішно доданий.%n", login);
                            } else if (params[1].equals("category")) {
                                String[] keys = Arrays.copyOfRange(params, 2, params.length);
                                String type = null;

                                try {
                                    for (int i = 0; i < keys.length; i++) {
                                        if (keys[i].equals("-t")) {
                                            type = keys[i + 1];
                                        }
                                    }

                                    if (type == null) {
                                        throw new Exception();
                                    }
                                } catch (Exception ex) {
                                    System.out.println("""
                                            Команда add category повинна мати наступний синатксис:
                                            add category -t <type>""");
                                    continue command;
                                }

                                try {
                                    controller.addCategory(type);
                                    System.out.printf("Категорія \"%s\" успішно додана.", type);
                                } catch (RuntimeException ex) {
                                    System.out.println("""
                                            Не вдалося додати нову категорію.
                                            Можливо, категорія такого типу вже існує.
                                            Будь ласка, придумайте нову категорію або відправте запит через декілька хвилин.""");
                                    continue command;
                                }
                            }
                        } else {
                            System.out.println("""
                                    Команда add повинна мати наступний синтаксис:
                                    add user -l <login> -p <password>
                                    add category -t <type>
                                    Для ознайомлення з повним списком команд введіть help""");
                        }
                    } else if (params[0].equals("confirm")) {
                        if (params.length >= 2) {
                            if (params[1].equals("--id")) {
                                try {
                                    int id = Integer.parseInt(params[2]);
                                    controller.confirmActivity(id);
                                    System.out.printf("Активність з id №%d успішно підтверджена.%n", id);
                                } catch (Exception ex) {
                                    System.out.println("""
                                            Параметр --id повинен приймате числове значення.
                                            Наприклад: confirm --id 10
                                            Для ознайомлення з повним списком команд введіть help""");
                                }
                            } else if (params[1].equals("-u")) {
                                try {
                                    String login = params[2];
                                    switch (params[3]) {
                                        case "--last" -> {
                                            controller.confirmActivity(login, RecordType.LAST);
                                            System.out.printf("Остання активність користувача \"%s\" " +
                                                    "успішно підтверджена.%n", login);
                                        }
                                        case "--all" -> {
                                            controller.confirmActivity(login, RecordType.ALL);
                                            System.out.printf("Всі активності користувача \"%s\" " +
                                                    "успішно підтверджені.%n", login);
                                        }
                                        default -> {
                                            System.out.println("""
                                                    Підтвердити активність або активності можна за допомогою ключів: --last, -all.
                                                    Для ознайомлення з повним списком команд введіть help""");
                                            continue command;
                                        }
                                    }
                                } catch (Exception ex) {
                                    System.out.println("""
                                            Команда confirm -u повинна мати наступний синтаксис:
                                            confirm -u <user_login> <--last | --all>
                                            Для ознайомлення з повним списком команд введіть help""");

                                }
                            } else {
                                System.out.println("""
                                        Команда confirm повинна мати наступний синтаксис:
                                        confirm --id <id>
                                        confirm -u <user_login> <--last | -all>
                                        Для ознайомлення з повним списком команд введіть help""");
                            }
                        } else {
                            System.out.println("""
                                    Команда confirm повинна мати наступний синтаксис:
                                    confirm --id <id>
                                    confirm -u <user_login> <--last | -all>
                                    Для ознайомлення з повним списком команд введіть help""");
                        }
                    } else if (params[0].equals("info")) {
                        if (params.length == 3 && params[1].equals("-u")) {
                            String login = params[2];
                            try {
                                List<Activity> activitiesByUser = controller.info(login);

                                System.out.printf("-------------------------------------------------%n" +
                                                "|%-47s|%n" +
                                                "-------------------------------------------------%n",
                                        StringUtils.center("Info about " + login, 47));

                                System.out.printf("|%-6s|%-16s|%-11s|%-11s|%n" +
                                                "-------------------------------------------------%n",
                                        StringUtils.center("id", 6),
                                        StringUtils.center("category", 16),
                                        StringUtils.center("duration", 11),
                                        StringUtils.center("confirmed", 11));

                                for (Activity activity : activitiesByUser) {
                                    int id = activity.getId();
                                    String categoryType = activity.getCategory().getType();
                                    int duration = activity.getDuration();
                                    boolean isConfirmed = activity.isConfirmed();

                                    System.out.printf("| %-5d| %-15s| %-10d| %-10b|%n" +
                                                    "-------------------------------------------------%n",
                                            id, categoryType, duration, isConfirmed);
                                }
                            } catch (Exception ex) {
                                System.out.println("""
                                        Команда info повинна мати наступний синтаксис:
                                        info -u <user_login>
                                        Для ознайомлення з повним списком команд введіть help""");
                            }
                        } else {
                            System.out.println("""
                                    Команда info повинна мати наступний синтаксис:
                                    info -u <user_login>
                                    Для ознайомлення з повним списком команд введіть help""");
                        }
                    } else if (params[0].equals("help")) {
                        System.out.printf("-------------------------%n" +
                                        "|%-23s|%n" +
                                        "-------------------------%n",
                                StringUtils.center("Список команд", 23));

                        System.out.println("""
                                1. list <users | categories | activities> - виводить список записів одного із запропонованих типів.
                                Ключі для list activities:
                                    --sort <category | user | duration> - сортує список за обраним параметром
                                    --asc (за замовчуванням) - сортує за зростанням
                                    --desc - сортує за спаданням
                                    
                                2. confirm - підтверджує активність.
                                Ключі:
                                    --id <id> - активність за її ідентифікаційним номером
                                    -u <user_login> <--last | -all> - активність користувача за обраним параметром
                                                            
                                3. add - додає користувача або категорію в базу даних.
                                Ключі:
                                    user -l <login> -p <password> - додає користувача
                                    category -t <type> - додає категорію
                                                            
                                4. info -u <user_login> - виводить список активностей обраного користувача.""");
                    } else if (params[0].equals("exit")) {
                        System.out.println("Ви вийшли із системи.");
                        continue auth;
                    } else if (!command.isBlank()) {
                        System.out.println("""
                                Невідома команда.
                                Для ознайомлення з повним списком команд введіть help""");
                    }
                }
            } else if (role == 2) {
                System.out.println("Ви бажаєте увійти в систему в якості користувача.");

                UserController controller;
                while (true) {
                    System.out.println("Введіть логін:");
                    String login = scanner.next();

                    System.out.println("Введіть пароль:");
                    String password = scanner.next();

                    controller = UserController.login(login, password);
                    if (controller == null) {
                        System.out.printf("Ви ввели неправильний логін \"%s\" або пароль \"%s\"\n", login, password);
                    } else {
                        System.out.printf("Користувач \"%s\" успішно авторизований.%n", login);
                        break;
                    }
                }

                command:
                while (true) {
                    String command = scanner.nextLine();
                    String[] params = command.split(" ");

                    if (params[0].equals("list")) {
                        List<Activity> activities = controller.getActivities();
                        System.out.printf("------------------------------------------------%n" +
                                        "|%-5s|%-16s|%-11s|%-11s|%n" +
                                        "------------------------------------------------%n",
                                " id ",
                                StringUtils.center("category", 16),
                                StringUtils.center("duration", 11),
                                StringUtils.center("confirmed", 11));

                        for (Activity activity : activities) {
                            System.out.printf("| %-4d| %-15s| %-10d| %-10b|%n" +
                                            "------------------------------------------------%n",
                                    activity.getId(), activity.getCategory().getType(),
                                    activity.getDuration(), activity.isConfirmed());
                        }
                    } else if (params[0].equals("add")) {
                        if (params.length == 5) {
                            String[] keys = Arrays.copyOfRange(params, 1, 5);
                            String type = null;
                            int duration = 0;

                            for (int i = 0; i < 4; i++) {
                                if (keys[i].equals("-t") && !keys[i + 1].equals("-d")) {
                                    type = keys[i + 1];
                                } else if (keys[i].equals("-d") && !keys[i + 1].equals("-t")) {
                                    try {
                                        duration = Integer.parseInt(keys[i + 1]);
                                    } catch (NumberFormatException ex) {
                                        System.out.println("""
                                                Ключ -d повинен приймати цілочисельне значення.
                                                Для ознайомлення з повним списком команд введіть help""");
                                        continue command;
                                    }
                                }
                            }

                            if (type == null || duration == 0) {
                                System.out.println("""
                                        Команда add повинна мати наступний синтаксис:
                                        add -t <category_type> -d <duration>
                                        Для ознайомлення з повним списком команд введіть help""");
                                continue command;
                            }

                            try {
                                controller.addActivity(type, duration);
                                System.out.printf("Активність типу \"%s\" (%d часів) успішно додана.%n",
                                        type, duration);
                            } catch (RuntimeException ex) {
                                List<Category> categories = controller.getCategories();

                                System.out.printf("Категорії \"%s\" не існує.%n", type);
                                System.out.println("Список наявних категорій: " +
                                        categories.stream().map(Category::getType).collect(Collectors.joining(", ")));
                                System.out.println("Для ознайомлення з повним списком команд введіть help");
                            }

                        } else {
                            List<Category> categories = controller.getCategories();

                            System.out.println("""
                                    Команда add повинна мати наступний синтаксис:
                                    add -t <category_type> -d <duration>""");
                            System.out.println("Список наявних категорій: " +
                                    categories.stream().map(Category::getType).collect(Collectors.joining(", ")));
                            System.out.println("Для ознайомлення з повним списком команд введіть help");
                        }
                    } else if (params[0].equals("delete")) {
                        if (params.length == 4) {
                            String[] keys = Arrays.copyOfRange(params, 1, 4);
                            RecordType recordType = null;
                            String type = null;

                            for (int i = 0; i < 3; i++) {
                                if (keys[i].equals("-t")) {
                                    type = keys[i + 1];
                                } else if (keys[i].equals("--last")) {
                                    recordType = RecordType.LAST;
                                } else if (keys[i].equals("--all")) {
                                    recordType = RecordType.ALL;
                                }
                            }

                            if (recordType == null || type == null) {
                                List<Category> categories = controller.getCategories();

                                System.out.println("""
                                        Команда delete повинна мати наступний синтаксис:
                                        delete -t <type> <--last | --all>""");
                                System.out.println("Список наявних категорій: " +
                                        categories.stream().map(Category::getType).collect(Collectors.joining(", ")));
                                System.out.println("Для ознайомлення з повним списком команд введіть help");

                                continue command;
                            }

                            try {
                                controller.deleteActivity(type, recordType);
                                if (recordType == RecordType.LAST) {
                                    System.out.printf("Остання активність типу \"%s\" успішно видалена.%n", type);
                                } else {
                                    System.out.printf("Всі активності типу \"%s\" успішно видалені.%n", type);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                List<Category> categories = controller.getCategories();

                                System.out.printf("Категорії \"%s\" не існує.%n", type);
                                System.out.println("Список наявних категорій: " +
                                        categories.stream().map(Category::getType).collect(Collectors.joining(", ")));
                                System.out.println("Для ознайомлення з повним списком команд введіть help");
                            }
                        } else {
                            List<Category> categories = controller.getCategories();

                            System.out.println("""
                                    Команда delete повинна мати наступний синтаксис:
                                    delete -t <type> <--last | --all>""");
                            System.out.println("Список наявних категорій:\n" +
                                    categories.stream().map(Category::getType).collect(Collectors.joining(", ")));
                            System.out.println("Для ознайомлення з повним списком команд введіть help");
                        }
                    } else if (params[0].equals("help")) {
                        System.out.printf("-------------------------%n" +
                                        "|%-23s|%n" +
                                        "-------------------------%n",
                                StringUtils.center("Список команд", 23));

                        System.out.println("""
                                1. list - виводить список активностей.
                                                            
                                2. add -t <type> -d <duration> - додає нову активність.
                                                            
                                3. delete -t <type> <--last | --all> - видаляє активність або активності за вказаними параметрами.""");
                    } else if (params[0].equals("exit")) {
                        System.out.println("Ви вийшли із системи.");
                        continue auth;
                    } else if (!command.isBlank()) {
                        System.out.println("""
                                Невідома команда.
                                Для ознайомлення з повним списком команд введіть help""");
                    }
                }
            }
        }
    }
}
