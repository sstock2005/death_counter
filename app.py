from ftplib import FTP_TLS
import os
import gzip
import shutil
import json
import discord
import configparser
from tqdm import tqdm

deaths = []

config = configparser.ConfigParser()
config.read('config.ini')
ftp_host = config.get('FTP', 'host')
ftp_port = config.getint('FTP', 'port')
ftp_user = config.get('FTP', 'user')
ftp_pass = config.get('FTP', 'pass')
remote_directory = config.get('FTP', 'remote_directory')
local_directory = config.get('FTP', 'local_directory')
token = config.get('DISCORD', 'token')
admin_id = config.getint('DISCORD', 'admin_id')

def get_logs(directory):
    for filename in os.listdir(directory):
        if filename.endswith('.log.gz'):
            with gzip.open(os.path.join(directory, filename), 'rb') as f_in:
                with open(os.path.join(directory, filename[:-3]), 'wb') as f_out:
                    shutil.copyfileobj(f_in, f_out)
            os.remove(os.path.join(directory, filename))

def download(ftp_host, ftp_port, ftp_user, ftp_pass, remote_directory, local_directory):
    ftp = FTP_TLS()
    ftp.connect(ftp_host, ftp_port)
    ftp.login(ftp_user, ftp_pass)
    ftp.prot_p()
    ftp.cwd(remote_directory)
    if not os.path.exists(local_directory):
        os.makedirs(local_directory)
    file_list = ftp.nlst()
    for file_name in file_list:
        local_file_path = os.path.join(local_directory, file_name)
        with open(local_file_path, 'wb') as local_file:
            ftp.retrbinary(f"RETR {file_name}", local_file.write)
    ftp.quit()
    get_logs(local_directory)

def calculate(log_directory, json_file):
    with open(json_file, 'r', encoding="utf-8") as f:
        json_data = json.load(f)

    files = os.listdir(log_directory)
    for filename in tqdm(files, desc="Processing files", unit="file"):
        if filename.endswith('.log'):
            with open(os.path.join(log_directory, filename), 'rb') as log_file:
                for line in log_file:
                    line = line.decode(errors='ignore')
                    line = line.encode('ascii', 'ignore').decode()
                    for key in json_data:
                        if key.startswith("death."):
                            if json_data[key].replace("%1$s", "").replace("%2$s", "").replace("%3$s", "").replace("%4$s", "").split("  ", 1)[0] in line:
                                if "Villager EntityVillager" in line:
                                    break
                                if "Async Chat Thread" in line:
                                    break
                                username = line.split("]: ")[1].split(" ")[0]
                                if "<" in username:
                                    username = username.split("<")[1].split(">")[0]
                                if "[DiscordSRV]" in username:
                                    break
                                deaths.append(username)
   

download(ftp_host, ftp_port, ftp_user, ftp_pass, remote_directory, local_directory)
calculate(local_directory, 'en_us.json')

for filename in os.listdir(local_directory):
    os.remove(os.path.join(local_directory, filename))

unique_deaths = list(dict.fromkeys(deaths))

counts = {}
for word in unique_deaths:
    if word in counts:
        counts[word] += 1
    else:
        counts[word] = 1

intents = discord.Intents.default()
intents.message_content = True

client = discord.Client(intents=intents)

@client.event
async def on_ready():
    print(f'We have logged in as {client.user}')

@client.event
async def on_message(message):
    if message.author == client.user:
        return

    if message.content.startswith('$getdeaths'):
        if message.author.id == admin_id:
            await message.channel.purge()
            await message.channel.send("**THIS WILL BE UPDATED DAILY**")
            embedVar = discord.Embed(title="Total Deaths", description="A list of people who need to be publicly humiliated", color=0x390707)
            if 'cooooootton' in counts:
                counts['cooooootton'] -= 2
            sorted_counts = dict(sorted(counts.items(), key=lambda item: item[1], reverse=True))
            for word, number in sorted_counts.items():
                if (number == 1):
                    embedVar.add_field(name=word, value=str(number) + " death", inline=False)
                else:
                    embedVar.add_field(name=word, value=str(number) + " deaths", inline=False)
            embedVar.set_footer(text="These guys are most unlucky, especially " + list(sorted_counts.keys())[0])
            embedVar.set_image(url="https://cdn.discordapp.com/attachments/909862307440496664/1201621244357918730/asdfasdf.jpg")
            await message.channel.send(embed=embedVar)

client.run(token)