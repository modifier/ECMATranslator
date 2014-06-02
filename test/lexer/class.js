class Enemy
{
    constructor()
    {
        console.log("Enemy.constructor");
    }

    attack(damage)
    {
        console.log(`Attack ${damage}`);
    }

    defend(defence)
    {
        return `Defence ${defence}`;
    }
}

class Zombie extends Enemy
{
    constructor()
    {
        super();
        console.log("Zombie.contructor");
    }

    defend(defence)
    {
        return `Zombie ${super.defend(defence)}`;
    }
}

var zombie = new Zombie();
console.log(zombie.defend(30));