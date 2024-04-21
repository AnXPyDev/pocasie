package com.example.pocasie.database;

import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class Seed {
    public static Random random = new Random();
    public static String[] locations = {"Bratislava","Košice","Prešov","Žilina","Nitra","Banská Bystrica"/*,"Trnava","Trenčín","Martin","Poprad","Prievidza","Zvolen","Považská Bystrica","Nové Zámky","Michalovce","Spišská Nová Ves","Komárno","Levice","Humenné","Bardejov","Liptovský Mikuláš","Piešťany","Ružomberok","Lučenec","Pezinok","Topoľčany","Dunajská Streda","Trebišov","Čadca","Dubnica nad Váhom","Rimavská Sobota","Partizánske","Vranov nad Topľou","Šaľa","Senec","Hlohovec","Brezno","Senica","Nové Mesto nad Váhom","Malacky","Snina","Dolný Kubín","Rožňava","Púchov","Žiar nad Hronom","Bánovce nad Bebravou","Handlová","Stará Ľubovňa","Skalica","Kežmarok","Sereď","Galanta","Kysucké Nové Mesto","Levoča","Detva","Šamorín","Stupava","Sabinov","Zlaté Moravce","Bytča","Revúca","Holíč","Myjava","Veľký Krtíš","Nová Dubnica","Kolárovo","Moldava nad Bodvou","Svidník","Stropkov","Fiľakovo","Štúrovo","Banská Štiavnica","Šurany","Modra","Tvrdošín","Krompachy","Veľké Kapušany","Sečovce","Stará Turá","Vráble","Veľký Meder","Svit","Námestovo","Krupina","Kráľovský Chlmec","Vrútky","Hurbanovo","Šahy","Hriňová","Turzovka","Liptovský Hrádok","Trstená","Nová Baňa","Tornaľa","Veľký Šariš","Spišská Belá","Želiezovce","Hnúšťa","Krásno nad Kysucou","Lipany","Nemšová","Turčianske Teplice","Svätý Jur","Sobrance","Gelnica","Rajec","Medzilaborce","Žarnovica","Vrbové","Ilava","Sládkovičovo","Gabčíkovo","Poltár","Dobšiná","Nesvady","Bojnice","Gbely","Šaštín-Stráže","Kremnica","Sliač","Brezová pod Bradlom","Strážske","Nováky","Turany","Medzev","Giraltovce","Trenčianske Teplice","Leopoldov","Vysoké Tatry","Spišské Podhradie","Hanušovce nad Topľou","Tisovec","Tlmače","Čierna nad Tisou","Spišské Vlachy","Jelšava","Podolínec","Rajecké Teplice","Spišská Stará Ves","Modrý Kameň","Dudince"*/};
    public static double baseTemp = 15.;
    public static double range = 3.;

    public static double getRangeOffset(double temp) {
        if (temp < baseTemp) {
            return (0 - Math.pow((temp - baseTemp), 3.)) / 30000.;
        } else {
            return (0 - Math.pow((temp - baseTemp), 3.)) / 15000.;
        }
    }

    public static double nextTemp(double temp) {
        return temp + random.nextDouble() * (range * 2) - range + getRangeOffset(temp) * range;
    }


    public static void seed(SQLiteDatabase db) {
        for (String name : locations) {
            Location location = new Location();
            location.name = name;

            location.push(db);

            Integer location_id = location.getID();

            LocalDate date_now = LocalDate.now();

            double temp = baseTemp;

            for (int d = 0; d < 15; d++) {
                for (int h = 0; h < 24; h++) {
                    temp = nextTemp(temp);

                    Datapoint datapoint = new Datapoint();
                    datapoint.location_id = location_id;
                    datapoint.temperature = (int)(temp * 100.d);
                    datapoint.weather = random.nextInt(Datapoint.weather_const.length);
                    datapoint.date = date_now.plusDays(d);
                    datapoint.time = LocalTime.MIDNIGHT.plusHours(h);
                    datapoint.push(db);
                }
            }

        }
    }

}
