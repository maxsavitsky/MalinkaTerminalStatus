# MalinkaTerminalStatus

This program displays some system information in terminal

Terminal separated into 2 sections: system status section and messages section.

Example:
![image](https://user-images.githubusercontent.com/38137967/155898556-34c6aaa8-1257-4a0a-a0e4-c2129dcb44bd.png)

On Linux you can also specify services which status will be displayed (see help for detailes):
![image](https://user-images.githubusercontent.com/38137967/155898532-822838e5-1dd7-4db5-a211-476bf58f7b86.png)

## Run
```console
$ git clone https://github.com/maxsavitsky/MalinkaTerminalStatus.git
$ cd MalinkaTerminalStatus
$ mvn package
$ java -jar target\MalinkaTerminalStatus-1.0-shaded.jar
```

## Help
You can also see this output if you specify `help` as the first parameter
```
ESC or Ctrl-X to exit (sometimes Enter should be pressed)

Arguments:
        --tty=<path to console dev block>
                Specifies where terminal should be displayed.
                Input and output streams will be redirected.
                For example, --tty=/dev/tty4
                Default is current terminal

        --temp-control-enabled=<true/false>
                Enables or disables temperature control (notifications, emergency shutdown).
                Default is true

        --temp-control-period=<period in seconds>
                Specifies how often temperature checks will take place
                Default is 15

        --system-status-update-period=<period in seconds>
                Specifies how often system information will be updated
                Default is 15

        --execute-after-startup=<command>
                This command will be executed after terminal will be ready to display data.

        --port=<port>
                Specifies port which socket will listen.
                Default is 8000

        --services-list=<path>
                This file describes the services whose status should be displayed.
                Each service is described by a separate line in the format id:name
                id should be identifier of service in systemctl
                Note: Works only on linux

        --mail-properties=<path>
                File describes mail configuration in key=value format.
                If not specified, notifications will not be sent
                It should contain:
                - mail - describes mail address on whose behalf the message will be sent
                - pass - password
                - recipients-list - comma-separated list of recipients
                - smtp-host - smtp host which will be used for mail sending (e.g. smtp.gmail.com)
```

## About name
'Malinka' is form of russian word 'malina' which means 'raspberry'
