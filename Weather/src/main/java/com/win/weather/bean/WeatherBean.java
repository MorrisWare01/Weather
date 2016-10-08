package com.win.weather.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by idea on 2016-09-08.
 */
public class WeatherBean {
    //注解了@SerializedName的字段会被序列化到JSON中，输出的JSON格式中的名字即为注解时给定的名字
    @SerializedName("HeWeather data service 3.0")
    public ArrayList<Weather> weathers;

    @Override
    public String toString() {
        return "WeatherBean{" +
                "weathers=" + weathers +
                '}';
    }

    public class Weather {
        public String status;
        public AqiBean aqi;
        public BasicBean basic;
        public NowBean now;
        public SuggestionBean suggestion;
        public ArrayList<DailyForecast> daily_forecast;
        public ArrayList<HourlyForecast> hourly_forecast;

        public class AqiBean {
            public CityBean city;

            @Override
            public String toString() {
                return "AqiBean{" +
                        "city=" + city +
                        '}';
            }

            public class CityBean {
                public String aqi;
                public String co;
                public String no2;
                public String o3;
                public String pm10;
                public String pm25;
                public String qlty;
                public String so2;

                @Override
                public String toString() {
                    return "CityBean{" +
                            "aqi='" + aqi + '\'' +
                            ", co='" + co + '\'' +
                            ", no2='" + no2 + '\'' +
                            ", o3='" + o3 + '\'' +
                            ", pm10='" + pm10 + '\'' +
                            ", pm25='" + pm25 + '\'' +
                            ", qlty='" + qlty + '\'' +
                            ", so2='" + so2 + '\'' +
                            '}';
                }
            }
        }

        public class BasicBean {
            public String city;
            public String cnty;
            public String id;
            public String lat;
            public String lon;
            public UpdateBean update;

            @Override
            public String toString() {
                return "BasicBean{" +
                        "city='" + city + '\'' +
                        ", cnty='" + cnty + '\'' +
                        ", id='" + id + '\'' +
                        ", lat='" + lat + '\'' +
                        ", lon='" + lon + '\'' +
                        ", update=" + update +
                        '}';
            }

            public class UpdateBean {
                public String loc;
                public String utc;

                @Override
                public String toString() {
                    return "UpdateBean{" +
                            "loc='" + loc + '\'' +
                            ", utc='" + utc + '\'' +
                            '}';
                }
            }
        }

        public class NowBean {
            public String fl;
            public String hum;
            public String pcpn;
            public String pres;
            public String tmp;
            public String vis;
            public CondBean cond;
            public WindBean wind;

            @Override
            public String toString() {
                return "NowBean{" +
                        "fl='" + fl + '\'' +
                        ", hum='" + hum + '\'' +
                        ", pcpn='" + pcpn + '\'' +
                        ", pres='" + pres + '\'' +
                        ", tmp='" + tmp + '\'' +
                        ", vis='" + vis + '\'' +
                        ", cond=" + cond +
                        ", wind=" + wind +
                        '}';
            }

            public class CondBean {
                public String code;
                public String txt;

                @Override
                public String toString() {
                    return "CondBean{" +
                            "code='" + code + '\'' +
                            ", txt='" + txt + '\'' +
                            '}';
                }
            }

            public class WindBean {
                public String deg;
                public String dir;
                public String sc;
                public String spd;

                @Override
                public String toString() {
                    return "WindBean{" +
                            "deg='" + deg + '\'' +
                            ", dir='" + dir + '\'' +
                            ", sc='" + sc + '\'' +
                            ", spd='" + spd + '\'' +
                            '}';
                }
            }
        }

        public class SuggestionBean {
            public TextBean comf;
            public TextBean cw;
            public TextBean drsg;
            public TextBean flu;
            public TextBean sport;
            public TextBean trav;
            public TextBean uv;

            @Override
            public String toString() {
                return "SuggestionBean{" +
                        "comf=" + comf +
                        ", cw=" + cw +
                        ", drsg=" + drsg +
                        ", flu=" + flu +
                        ", sport=" + sport +
                        ", trav=" + trav +
                        ", uv=" + uv +
                        '}';
            }

            public class TextBean {
                public String brf;
                public String txt;

                @Override
                public String toString() {
                    return "TextBean{" +
                            "brf='" + brf + '\'' +
                            ", txt='" + txt + '\'' +
                            '}';
                }
            }
        }

        public class HourlyForecast {
            public String date;
            public String hum;
            public String pop;
            public String pres;
            public String tmp;
            public NowBean.WindBean wind;

            @Override
            public String toString() {
                return "HourlyForecast{" +
                        "date='" + date + '\'' +
                        ", hum='" + hum + '\'' +
                        ", pop='" + pop + '\'' +
                        ", pres='" + pres + '\'' +
                        ", tmp='" + tmp + '\'' +
                        ", wind=" + wind +
                        '}';
            }
        }

        public class DailyForecast {
            public String date;
            public String hum;
            public String pcpn;
            public String pop;
            public String pres;
            public String vis;
            public TmpBean tmp;
            public AstroBean astro;
            public CondBean cond;
            public NowBean.WindBean wind;

            @Override
            public String toString() {
                return "DailyForecast{" +
                        "date='" + date + '\'' +
                        ", hum='" + hum + '\'' +
                        ", pcpn='" + pcpn + '\'' +
                        ", pop='" + pop + '\'' +
                        ", pres='" + pres + '\'' +
                        ", vis='" + vis + '\'' +
                        ", tmp=" + tmp +
                        ", astro=" + astro +
                        ", cond=" + cond +
                        ", wind=" + wind +
                        '}';
            }

            public class TmpBean {
                public String max;
                public String min;

                @Override
                public String toString() {
                    return "TmpBean{" +
                            "max='" + max + '\'' +
                            ", min='" + min + '\'' +
                            '}';
                }
            }

            public class AstroBean {
                public String sr;
                public String ss;

                @Override
                public String toString() {
                    return "AstroBean{" +
                            "sr='" + sr + '\'' +
                            ", ss='" + ss + '\'' +
                            '}';
                }
            }

            public class CondBean {
                public String code_d;
                public String code_n;
                public String txt_d;
                public String txt_n;

                @Override
                public String toString() {
                    return "CondBean{" +
                            "code_d='" + code_d + '\'' +
                            ", code_n='" + code_n + '\'' +
                            ", txt_d='" + txt_d + '\'' +
                            ", txt_n='" + txt_n + '\'' +
                            '}';
                }
            }
        }

        @Override
        public String toString() {
            return "Weather{" +
                    "status='" + status + '\'' +
                    ", aqi=" + aqi +
                    ", basic=" + basic +
                    ", now=" + now +
                    ", suggestion=" + suggestion +
                    ", daily_forecast=" + daily_forecast +
                    ", hourly_forecast=" + hourly_forecast +
                    '}';
        }
    }

}
