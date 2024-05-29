import asyncio
import logging
import os
import re
from json import dumps

import nltk
from rsocket.helpers import create_future
from rsocket.payload import Payload
from rsocket.request_handler import BaseRequestHandler
from rsocket.rsocket_server import RSocketServer
from rsocket.transports.tcp import TransportTCP

logging.basicConfig(level=logging.INFO)
nltk.download("punkt")
nltk.download("averaged_perceptron_tagger_ru")

rus = {
    "S": "сущ.",
    "A": "прил.",
    "NUM": "числ.",
    "A-NUM": "числ.-прил.",
    "V": "глаг.",
    "ADV": "нареч.",
    "PRAEDIC": "предикатив",
    "PARENTH": "вводное",
    "S-PRO": "местоим. сущ.",
    "A-PRO": "местоим. прил.",
    "ADV-PRO": "местоим. нареч.",
    "PRAEDIC-PRO": "местоим. предик.",
    "PR": "предлог",
    "CONJ": "союз",
    "PART": "частица",
    "INTJ": "межд.",
    "INIT": "иниц.",
    "NONLEX": "нонлекс"
}

std_encoding = 'utf-8'
std_host = '127.0.0.1'
std_port = '9999'
std_tokenize_lang = 'rus'
speech_part_delimiter = '='
noise_removal_pattern = r"[^А-Яа-яЁё\-\n\s]+"


def find_speech_part(text: str):
    # Устранение шума в тексте, разбиение на слова и перевод в нижний регистр
    text = re.sub(noise_removal_pattern, "", text)

    # Определение частей речи в тексте
    words_eng_tag = nltk.pos_tag(nltk.word_tokenize(text), lang=std_tokenize_lang)

    words_ru_tag = []
    for word in words_eng_tag:
        speech_part = word[1]
        index = speech_part.find(speech_part_delimiter)
        if index != -1:
            speech_part = speech_part[:index]
        words_ru_tag.append((word[0], rus[speech_part]))

    return words_ru_tag


class Handler(BaseRequestHandler):
    async def request_response(self, payload: Payload) -> asyncio.Future:
        text = payload.data.decode(std_encoding)
        speech_parts = find_speech_part(text)
        json_response = dumps(speech_parts, ensure_ascii=False)
        return create_future(Payload(json_response.encode(std_encoding)))


async def run_server(server_host, server_port):
    logging.info('Starting server at %s:%s', server_host, server_port)

    def session(*connection):
        RSocketServer(TransportTCP(*connection), handler_factory=Handler)

    server = await asyncio.start_server(session, server_host, server_port)

    async with server:
        await server.serve_forever()


if __name__ == '__main__':
    host = os.getenv('host', std_host)
    port = os.getenv('port', std_port)
    asyncio.run(run_server(host, port))
