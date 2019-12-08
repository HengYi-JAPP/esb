FROM mcr.microsoft.com/java/jre:11u5-zulu-centos

#RUN rm -rf /etc/localtime && ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
#    && yum -y install kde-l10n-Chinese && yum -y reinstall glibc-common \
#    && localedef -c -f UTF-8 -i zh_CN zh_CN.utf8
#
#ENV LC_ALL zh_CN.utf8 #设置环境变量

#ENV LANG     zh_CN.UTF-8
#ENV LANGUAGE zh_CN.UTF-8
#ENV LC_ALL   zh_CN.UTF-8
ENV TZ Asia/Shanghai
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport"
ENV JAVA_OPTIONS ""

ENV LD_LIBRARY_PATH /sapjco3
COPY libsapjco3.so $LD_LIBRARY_PATH/
RUN  echo "sapdp00  3200/tcp" >> /etc/services \
    && echo "sapdp01  3201/tcp" >> /etc/services \
    && echo "sapdp02  3202/tcp" >> /etc/services \
    && echo "sapdp03  3203/tcp" >> /etc/services \
    && echo "sapdp04  3204/tcp" >> /etc/services \
    && echo "sapdp05  3205/tcp" >> /etc/services \
    && echo "sapdp06  3206/tcp" >> /etc/services \
    && echo "sapdp07  3207/tcp" >> /etc/services \
    && echo "sapdp08  3208/tcp" >> /etc/services \
    && echo "sapdp09  3209/tcp" >> /etc/services \
    && echo "sapdp10  3210/tcp" >> /etc/services \
    && echo "sapdp11  3211/tcp" >> /etc/services \
    && echo "sapdp12  3212/tcp" >> /etc/services \
    && echo "sapdp13  3213/tcp" >> /etc/services \
    && echo "sapdp14  3214/tcp" >> /etc/services \
    && echo "sapdp15  3215/tcp" >> /etc/services \
    && echo "sapdp16  3216/tcp" >> /etc/services \
    && echo "sapdp17  3217/tcp" >> /etc/services \
    && echo "sapdp18  3218/tcp" >> /etc/services \
    && echo "sapdp19  3219/tcp" >> /etc/services \
    && echo "sapdp20  3220/tcp" >> /etc/services \
    && echo "sapdp21  3221/tcp" >> /etc/services \
    && echo "sapdp22  3222/tcp" >> /etc/services \
    && echo "sapdp23  3223/tcp" >> /etc/services \
    && echo "sapdp24  3224/tcp" >> /etc/services \
    && echo "sapdp25  3225/tcp" >> /etc/services \
    && echo "sapdp26  3226/tcp" >> /etc/services \
    && echo "sapdp27  3227/tcp" >> /etc/services \
    && echo "sapdp28  3228/tcp" >> /etc/services \
    && echo "sapdp29  3229/tcp" >> /etc/services \
    && echo "sapdp30  3230/tcp" >> /etc/services \
    && echo "sapdp31  3231/tcp" >> /etc/services \
    && echo "sapdp32  3232/tcp" >> /etc/services \
    && echo "sapdp33  3233/tcp" >> /etc/services \
    && echo "sapdp34  3234/tcp" >> /etc/services \
    && echo "sapdp35  3235/tcp" >> /etc/services \
    && echo "sapdp36  3236/tcp" >> /etc/services \
    && echo "sapdp37  3237/tcp" >> /etc/services \
    && echo "sapdp38  3238/tcp" >> /etc/services \
    && echo "sapdp39  3239/tcp" >> /etc/services \
    && echo "sapdp40  3240/tcp" >> /etc/services \
    && echo "sapdp41  3241/tcp" >> /etc/services \
    && echo "sapdp42  3242/tcp" >> /etc/services \
    && echo "sapdp43  3243/tcp" >> /etc/services \
    && echo "sapdp44  3244/tcp" >> /etc/services \
    && echo "sapdp45  3245/tcp" >> /etc/services \
    && echo "sapdp46  3246/tcp" >> /etc/services \
    && echo "sapdp47  3247/tcp" >> /etc/services \
    && echo "sapdp48  3248/tcp" >> /etc/services \
    && echo "sapdp49  3249/tcp" >> /etc/services \
    && echo "sapdp50  3250/tcp" >> /etc/services \
    && echo "sapdp51  3251/tcp" >> /etc/services \
    && echo "sapdp52  3252/tcp" >> /etc/services \
    && echo "sapdp53  3253/tcp" >> /etc/services \
    && echo "sapdp54  3254/tcp" >> /etc/services \
    && echo "sapdp55  3255/tcp" >> /etc/services \
    && echo "sapdp56  3256/tcp" >> /etc/services \
    && echo "sapdp57  3257/tcp" >> /etc/services \
    && echo "sapdp58  3258/tcp" >> /etc/services \
    && echo "sapdp59  3259/tcp" >> /etc/services \
    && echo "sapdp60  3260/tcp" >> /etc/services \
    && echo "sapdp61  3261/tcp" >> /etc/services \
    && echo "sapdp62  3262/tcp" >> /etc/services \
    && echo "sapdp63  3263/tcp" >> /etc/services \
    && echo "sapdp64  3264/tcp" >> /etc/services \
    && echo "sapdp65  3265/tcp" >> /etc/services \
    && echo "sapdp66  3266/tcp" >> /etc/services \
    && echo "sapdp67  3267/tcp" >> /etc/services \
    && echo "sapdp68  3268/tcp" >> /etc/services \
    && echo "sapdp69  3269/tcp" >> /etc/services \
    && echo "sapdp70  3270/tcp" >> /etc/services \
    && echo "sapdp71  3271/tcp" >> /etc/services \
    && echo "sapdp72  3272/tcp" >> /etc/services \
    && echo "sapdp73  3273/tcp" >> /etc/services \
    && echo "sapdp74  3274/tcp" >> /etc/services \
    && echo "sapdp75  3275/tcp" >> /etc/services \
    && echo "sapdp76  3276/tcp" >> /etc/services \
    && echo "sapdp77  3277/tcp" >> /etc/services \
    && echo "sapdp78  3278/tcp" >> /etc/services \
    && echo "sapdp79  3279/tcp" >> /etc/services \
    && echo "sapdp80  3280/tcp" >> /etc/services \
    && echo "sapdp81  3281/tcp" >> /etc/services \
    && echo "sapdp82  3282/tcp" >> /etc/services \
    && echo "sapdp83  3283/tcp" >> /etc/services \
    && echo "sapdp84  3284/tcp" >> /etc/services \
    && echo "sapdp85  3285/tcp" >> /etc/services \
    && echo "sapdp86  3286/tcp" >> /etc/services \
    && echo "sapdp87  3287/tcp" >> /etc/services \
    && echo "sapdp88  3288/tcp" >> /etc/services \
    && echo "sapdp89  3289/tcp" >> /etc/services \
    && echo "sapdp90  3290/tcp" >> /etc/services \
    && echo "sapdp91  3291/tcp" >> /etc/services \
    && echo "sapdp92  3292/tcp" >> /etc/services \
    && echo "sapdp93  3293/tcp" >> /etc/services \
    && echo "sapdp94  3294/tcp" >> /etc/services \
    && echo "sapdp95  3295/tcp" >> /etc/services \
    && echo "sapdp96  3296/tcp" >> /etc/services \
    && echo "sapdp97  3297/tcp" >> /etc/services \
    && echo "sapdp98  3298/tcp" >> /etc/services \
    && echo "sapdp99  3299/tcp" >> /etc/services \
    && echo "sapgw00  3300/tcp" >> /etc/services \
    && echo "sapgw01  3301/tcp" >> /etc/services \
    && echo "sapgw02  3302/tcp" >> /etc/services \
    && echo "sapgw03  3303/tcp" >> /etc/services \
    && echo "sapgw04  3304/tcp" >> /etc/services \
    && echo "sapgw05  3305/tcp" >> /etc/services \
    && echo "sapgw06  3306/tcp" >> /etc/services \
    && echo "sapgw07  3307/tcp" >> /etc/services \
    && echo "sapgw08  3308/tcp" >> /etc/services \
    && echo "sapgw09  3309/tcp" >> /etc/services \
    && echo "sapgw10  3310/tcp" >> /etc/services \
    && echo "sapgw11  3311/tcp" >> /etc/services \
    && echo "sapgw12  3312/tcp" >> /etc/services \
    && echo "sapgw13  3313/tcp" >> /etc/services \
    && echo "sapgw14  3314/tcp" >> /etc/services \
    && echo "sapgw15  3315/tcp" >> /etc/services \
    && echo "sapgw16  3316/tcp" >> /etc/services \
    && echo "sapgw17  3317/tcp" >> /etc/services \
    && echo "sapgw18  3318/tcp" >> /etc/services \
    && echo "sapgw19  3319/tcp" >> /etc/services \
    && echo "sapgw20  3320/tcp" >> /etc/services \
    && echo "sapgw21  3321/tcp" >> /etc/services \
    && echo "sapgw22  3322/tcp" >> /etc/services \
    && echo "sapgw23  3323/tcp" >> /etc/services \
    && echo "sapgw24  3324/tcp" >> /etc/services \
    && echo "sapgw25  3325/tcp" >> /etc/services \
    && echo "sapgw26  3326/tcp" >> /etc/services \
    && echo "sapgw27  3327/tcp" >> /etc/services \
    && echo "sapgw28  3328/tcp" >> /etc/services \
    && echo "sapgw29  3329/tcp" >> /etc/services \
    && echo "sapgw30  3330/tcp" >> /etc/services \
    && echo "sapgw31  3331/tcp" >> /etc/services \
    && echo "sapgw32  3332/tcp" >> /etc/services \
    && echo "sapgw33  3333/tcp" >> /etc/services \
    && echo "sapgw34  3334/tcp" >> /etc/services \
    && echo "sapgw35  3335/tcp" >> /etc/services \
    && echo "sapgw36  3336/tcp" >> /etc/services \
    && echo "sapgw37  3337/tcp" >> /etc/services \
    && echo "sapgw38  3338/tcp" >> /etc/services \
    && echo "sapgw39  3339/tcp" >> /etc/services \
    && echo "sapgw40  3340/tcp" >> /etc/services \
    && echo "sapgw41  3341/tcp" >> /etc/services \
    && echo "sapgw42  3342/tcp" >> /etc/services \
    && echo "sapgw43  3343/tcp" >> /etc/services \
    && echo "sapgw44  3344/tcp" >> /etc/services \
    && echo "sapgw45  3345/tcp" >> /etc/services \
    && echo "sapgw46  3346/tcp" >> /etc/services \
    && echo "sapgw47  3347/tcp" >> /etc/services \
    && echo "sapgw48  3348/tcp" >> /etc/services \
    && echo "sapgw49  3349/tcp" >> /etc/services \
    && echo "sapgw50  3350/tcp" >> /etc/services \
    && echo "sapgw51  3351/tcp" >> /etc/services \
    && echo "sapgw52  3352/tcp" >> /etc/services \
    && echo "sapgw53  3353/tcp" >> /etc/services \
    && echo "sapgw54  3354/tcp" >> /etc/services \
    && echo "sapgw55  3355/tcp" >> /etc/services \
    && echo "sapgw56  3356/tcp" >> /etc/services \
    && echo "sapgw57  3357/tcp" >> /etc/services \
    && echo "sapgw58  3358/tcp" >> /etc/services \
    && echo "sapgw59  3359/tcp" >> /etc/services \
    && echo "sapgw60  3360/tcp" >> /etc/services \
    && echo "sapgw61  3361/tcp" >> /etc/services \
    && echo "sapgw62  3362/tcp" >> /etc/services \
    && echo "sapgw63  3363/tcp" >> /etc/services \
    && echo "sapgw64  3364/tcp" >> /etc/services \
    && echo "sapgw65  3365/tcp" >> /etc/services \
    && echo "sapgw66  3366/tcp" >> /etc/services \
    && echo "sapgw67  3367/tcp" >> /etc/services \
    && echo "sapgw68  3368/tcp" >> /etc/services \
    && echo "sapgw69  3369/tcp" >> /etc/services \
    && echo "sapgw70  3370/tcp" >> /etc/services \
    && echo "sapgw71  3371/tcp" >> /etc/services \
    && echo "sapgw72  3372/tcp" >> /etc/services \
    && echo "sapgw73  3373/tcp" >> /etc/services \
    && echo "sapgw74  3374/tcp" >> /etc/services \
    && echo "sapgw75  3375/tcp" >> /etc/services \
    && echo "sapgw76  3376/tcp" >> /etc/services \
    && echo "sapgw77  3377/tcp" >> /etc/services \
    && echo "sapgw78  3378/tcp" >> /etc/services \
    && echo "sapgw79  3379/tcp" >> /etc/services \
    && echo "sapgw80  3380/tcp" >> /etc/services \
    && echo "sapgw81  3381/tcp" >> /etc/services \
    && echo "sapgw82  3382/tcp" >> /etc/services \
    && echo "sapgw83  3383/tcp" >> /etc/services \
    && echo "sapgw84  3384/tcp" >> /etc/services \
    && echo "sapgw85  3385/tcp" >> /etc/services \
    && echo "sapgw86  3386/tcp" >> /etc/services \
    && echo "sapgw87  3387/tcp" >> /etc/services \
    && echo "sapgw88  3388/tcp" >> /etc/services \
    && echo "sapgw89  3389/tcp" >> /etc/services \
    && echo "sapgw90  3390/tcp" >> /etc/services \
    && echo "sapgw91  3391/tcp" >> /etc/services \
    && echo "sapgw92  3392/tcp" >> /etc/services \
    && echo "sapgw93  3393/tcp" >> /etc/services \
    && echo "sapgw94  3394/tcp" >> /etc/services \
    && echo "sapgw95  3395/tcp" >> /etc/services \
    && echo "sapgw96  3396/tcp" >> /etc/services \
    && echo "sapgw97  3397/tcp" >> /etc/services \
    && echo "sapgw98  3398/tcp" >> /etc/services \
    && echo "sapgw99  3399/tcp" >> /etc/services \
    && echo "sapmsEP1 	3600/tcp" >> /etc/services

#ENV VERTICLE_HOME /usr/verticles
#ENV VERTICLE_FILE esb-sap-fat.jar
#COPY $VERTICLE_FILE $VERTICLE_HOME/

#EXPOSE 9876
#EXPOSE 9997

#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec java --add-modules java.se \
#--add-exports java.base/jdk.internal.ref=ALL-UNNAMED \
#--add-opens java.base/java.lang=ALL-UNNAMED \
#--add-opens java.base/java.lang.invoke=ALL-UNNAMED \
#--add-opens java.base/java.nio=ALL-UNNAMED \
#--add-opens java.base/sun.nio.ch=ALL-UNNAMED \
#--add-opens java.management/sun.management=ALL-UNNAMED \
#--add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED \
#$JAVA_OPTIONS -jar $VERTICLE_FILE -conf /home/esb/esb-sap/config.json -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.jmxEnabled=true"]
