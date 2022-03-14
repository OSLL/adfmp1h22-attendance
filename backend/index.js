const express = require('express')
const fs = require('fs')
const Users = require('./database/users.json')
const Lessons = require('./database/lessons.json')
const Checks = require('./database/checks.json')
const { use } = require('express/lib/application')

const port = 3001

const app = express()

function logger(req, res, next) {
    console.log(`[${Date.now()}] ${req.method} ${req.url}`)
    console.log("No")
    next()
}

app.use(logger)
app.use(express.json())

const TEACHERS = ['ks@mail.ru']

app.post('/users/register', (req, res) => {
    data = req.body
    console.log(data)
    if(Users.filter(user => user.email === data.email).length === 0){
        const newUser = {
            id: `${Users.length}`,
            email: data.email,
            password: data.password,
            firstname: data.firstname,
            lastname: data.lastname,
            secondname: data.secondname,
            group: "1",
            telnum: data.telnum,
            status: TEACHERS.includes(data.email) ? "Teacher" : "Student"
        }
        Users.push(newUser)
        fs.writeFile('./database/users.json', JSON.stringify(Users, null, 4), 'utf8', (err) => {
            if(err){
                console.log(err)
                console.log("Error writing to users.json")
                res.status(500)
            } else {
                res.status(201)
                res.send(JSON.stringify(newUser))
            }
        })
    } else {
        res.status(400)
    }
    res.status(403)
})

app.post('/users/modify', (req, res) => {
    data = req.body
    console.log(data)
    if(Users.filter(user => user.email === data.email).length === 0){
        Users.forEach(element => {
            if(element.id == data.id){
                element.surname = data.surname
                element.firstname = data.firstname
                element.secondname = data.secondname
                element.email = data.email
                element.telnum = data.telnum
                element.status = TEACHERS.includes(data.email) ? "Teacher" : "Student"
                fs.writeFile('./database/users.json', JSON.stringify(Users, null, 4), 'utf8', (err) => {
                    if(err){
                        console.log(err)
                        console.log("Error writing to users.json")
                        res.status(500)
                    } else {
                        res.status(202)
                        res.send(JSON.stringify(element))
                    }
                })
            } else {
                res.send(400)
            }
        })
    } else {
        res.send(400)
    }
})

app.post('/users/login', (req, res) => {
    data = req.body
    console.log(data)
    if(Users.filter(user => user.email === data.email).length !== 0){
        user = Users.filter(user => user.email === data.email)[0]
        if(user.password === data.password){
            res.status(200)
            res.send(JSON.stringify(user))
        } else {
            res.status(403)
        }
    }
    res.status(403)
})

app.post('/lessons/add', (req, res) => {
    data = req.body
    newLesson = {
        id: `${Lessons.length}`,
        name: data.name,
        group: data.group,
        date: data.date,
        time: data.time,
        position: {
            x: `${Lessons.length}`,
            y: `${Lessons.length}`
        }
    }
    Lessons.push(newLesson)
    fs.writeFile('./database/lessons.json', JSON.stringify(Lessons, null, 4), 'utf8', (err) => {
        if(err){
            console.log(err)
            console.log("Error writing to lessons.json")
            res.status(500)
        } else {
            res.status(202)
            res.send(JSON.stringify(newLesson))
        }
    })
})

app.get('/lessons/:group/:userId', (req, res) => {
    group = req.params.group
    userId = req.params.userId
    const lessons = Lessons.filter(lesson => lesson.group === group)
    const lessonsChecked = Checks.filter(check => check.userId === userId)
        .map(check => check.lessonId)
    const lessonsDif = lessons.filter(lesson => !lessonsChecked.includes(lesson.id))
    res.status(200)
    res.send(JSON.stringify(lessonsDif))
})

function checkCoords(data, lessonId) {
    return true
}

app.post('/lessons/check/:lessonId/:userId', (req, res) => {
    lessonId = req.params.lessonId
    userId = req.params.userId
    data = req.body
    if(Lessons.filter(lesson => lesson.id === lessonId).length > 0 &&
        Users.filter(user => user.id === userId).length > 0){
            if(checkCoords(data, lessonId)){
                Checks.push({
                    id: `${Checks.length}`,
                    lessonId: lessonId,
                    userId: userId
                })
                fs.writeFile('./database/checks.json', JSON.stringify(Checks, null, 4), 'utf8', (err) => {
                    if(err){
                        console.log("Error writing to checks.json")
                        res.status(500)
                    } else {
                        res.status(201)
                    }
                })
            } else {
                res.status(400)
            }
    } else {
        res.status(400)
    }
})

app.listen(port, () => console.log('Server listening on port ' + port))