#
FROM python:3.12

#
WORKDIR /code

#
COPY ./requirements.txt /code/requirements.txt

#
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt

#
COPY ./app /code/app

ARG host="0.0.0.0"
ARG port="9999"
ENV host=$host
ENV port=$port

#
CMD ["python", "app/main.py"]

# docker build -t nltk-server .
