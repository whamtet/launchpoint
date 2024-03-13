const puppeteer = require('puppeteer');
const {assert} = require('chai');

const {typeInput, clickValue} = require('./util');
const {profile} = require('./profile');

const prepare = async () => {
  // delete test user
  const response = await fetch('http://localhost:3001/api/user/2');
  assert.equal(response.status, 200);
};

async function register(page) {

  page.click('#register');
  await page.waitForSelector('#registration-form');

  await typeInput(page, 'first-name', 'Test');
  await typeInput(page, 'last-name', 'User');
  await typeInput(page, 'email', 'test@example.com');
  await typeInput(page, 'password', 'asdf1234');
  await typeInput(page, 'password2', 'asdf1234');

  await clickValue(page, 'Register');
  await page.waitForNavigation();

}

(async () => {

  await prepare();

  // Launch the browser and open a new blank page
  const browser = await puppeteer.launch({headless: false});
  const page = await browser.newPage();

  await page.goto('http://localhost:3001/');
  await page.setViewport({width: 1440, height: 1024});

  await register(page);

  await profile(page);

  // console.log('all tests passed');

  // browser.close();

})();
