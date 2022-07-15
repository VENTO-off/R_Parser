package relevant_craft.vento.r_launcher.r_parser.utils;

import java.util.HashMap;

public class Translator {

    private static HashMap<String, String> categories = new HashMap<>();

    private static void initCategoriesTranslate() {
        if (categories.isEmpty()) {
            categories.put("mc-addons",                                 "Аддоны");
            categories.put("applied-energistics-2",                     "      Applied Energistics 2");
            categories.put("blood-magic",                               "      Blood Magic");
            categories.put("addons-buildcraft",                         "      Buildcraft");
            categories.put("crafttweaker",                              "      CraftTweaker");
            categories.put("addons-forestry",                           "      Forestry");
            categories.put("addons-industrialcraft",                    "      Industrial Craft");
            categories.put("addons-thaumcraft",                         "      Thaumcraft");
            categories.put("addons-thermalexpansion",                   "      Thermal Expansion");
            categories.put("addons-tinkers-construct",                  "      Tinker's Construct");
            categories.put("adventure-rpg",                             "Приключения и RPG");
            categories.put("armor-weapons-tools",                       "Броня, Инструменты, Оружие");
            categories.put("cosmetic",                                  "Декоративное");
            categories.put("mc-food",                                   "Еда");
            categories.put("magic",                                     "Магия");
            categories.put("map-information",                           "Карта и информация");
            categories.put("redstone",                                  "Редстоун");
            categories.put("server-utility",                            "Утилиты для Сервера");
            categories.put("storage",                                   "Хранилища");
            categories.put("technology",                                "Технологии");
            categories.put("technology-energy",                         "      Энергия");
            categories.put("technology-item-fluid-energy-transport",    "      Транспортировка Предметов");
            categories.put("technology-farming",                        "      Фермерство");
            categories.put("technology-genetics",                       "      Генетика");
            categories.put("technology-player-transport",               "      Транспортировка Игроков");
            categories.put("technology-processing",                     "      Переработка");
            categories.put("twitch-integration",                        "Интеграция с Twitch");
            categories.put("world-gen",                                 "Генерация Мира");
            categories.put("world-biomes",                              "      Биомы");
            categories.put("world-dimensions",                          "      Измерения");
            categories.put("world-mobs",                                "      Мобы");
            categories.put("world-ores-resources",                      "      Руды и Ресурсы");
            categories.put("world-structures",                          "      Структуры");
            categories.put("library-api",                               "API и Библиотеки");
            categories.put("fabric",                                    "Fabric");
            categories.put("mc-miscellaneous",                          "Другое");
            categories.put("sixteen-x",                                 "16x");
            categories.put("thirty-two-x",                              "32x");
            categories.put("sixty-four-x",                              "64x");
            categories.put("one-twenty-eight-x",                        "128x");
            categories.put("two-fifty-six-x",                           "256x");
            categories.put("five-twelve-x-and-beyond",                  "512x и Выше");
            categories.put("animated",                                  "Анимированные");
            categories.put("medieval",                                  "Средневековые");
            categories.put("mod-support",                               "Для Модов");
            categories.put("modern",                                    "Современные");
            categories.put("photo-realistic",                           "Реалистичные");
            categories.put("steampunk",                                 "Стимпанк");
            categories.put("traditional",                               "Обычные");
            categories.put("miscellaneous",                             "Другое");
            categories.put("adventure",                                 "Приключения");
            categories.put("creation",                                  "Постройки");
            categories.put("game-map",                                  "Игровые Карты");
            categories.put("modded-world",                              "Мир с Модами");
            categories.put("parkour",                                   "Паркур");
            categories.put("puzzle",                                    "Пазлы");
            categories.put("survival",                                  "Выживание");
            categories.put("adventure-and-rpg",                         "Приключения и RPG");
            categories.put("combat-pvp",                                "Сражения / PVP");
            categories.put("exploration",                               "Исследования");
            categories.put("extra-large",                               "Большие сборки");
            categories.put("hardcore",                                  "Хардкор");
            categories.put("map-based",                                 "С Картой");
            categories.put("mini-game",                                 "Мини-игры");
            categories.put("multiplayer",                               "Мультиплеер");
            categories.put("quests",                                    "Квесты");
            categories.put("sci-fi",                                    "Научно-фантастические");
            categories.put("skyblock",                                  "SkyBlock");
            categories.put("small-light",                               "Лёгкие сборки");
            categories.put("tech",                                      "Технические");
            categories.put("ftb-official-pack",                         "Официальные FTB сборки");
        }
    }

    public static String translateToRussian(String id) {
        initCategoriesTranslate();
        if (categories.containsKey(id)) {
            return categories.get(id);
        }
        return id;
    }
}
