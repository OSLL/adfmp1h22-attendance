const express = require('express')
const fs = require('fs')
const Users = require('./database/users.json')
const Lessons = require('./database/lessons.json')

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

app.post('/users', (req, res) => {
    console.log("Yes")
    data = req.body
    console.log(data)
    if(Users.filter(user => user.email === data.email).length === 0){
        const newUser = {
            id: Users.length,
            email: data.email,
            password: data.password,
            firstname: data.firstname,
            lastname: data.lastname,
            secondname: data.secondname,
            group: 1,
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
    }
    res.status(403)
})

app.get('/lessons/:group', (req, res) => {
    res.json(Lessons.filter(lesson => lesson["group"] === req.params.group))
})

app.post('/lessons/:group', (req, res) => {
    data = req.body.json()
    Lessons.push({
        id: Lessons.length,
        name: data.name,
        group: req.params.group,
        date: data.date,
        time: data.time,
    })
    fs.writeFile('./database/lessons.json', JSON.stringify(Lessons, null, 4), 'utf8', (err) => {
        console.log("Error writing to lessons.json")
        res.status(500)
    })
    res.status(201)
})

app.listen(port, () => console.log('Server listening on port ' + port))